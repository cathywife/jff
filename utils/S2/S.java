/**
 * Ԥ��������� class �ļ���ȥ��һЩ��������ļ��ɣ�
 * ���� jad.exe �����롣
 *
 * �������� www.apache.org �� BCEL �ֽ��봦��⡣
 *
 * �÷���
 *  ����
 *  javac -classpath bcel-5.2.jar S.java
 *  
 *  ����
 *      ���罫 bcel, S.class ���ڸ�Ŀ¼����ǰ���� game Ŀ¼��
 *      set CLASSPATH=c:\WTK22\lib\cldcapi11.jar;c:\WTK22\lib\midpapi20.jar
 *      dir /s/b *.class | java -classpath %CLASSPATH%;..\bcel-5.2.jar;..;. S
 *
 *      �� UNIX ��ϵͳ�� classpath ��ð�ŷָ���ִ�������Ӧ�� class �ļ���
 *      ֱ���滻��������Ԥ�ȱ��ݡ�
 *
 * 20070325 lyb 0.1 �ϲ��쳣��
 * 20070403 lyb 0.9 ���������Ա
 *
 * BUG:
 *      Ч�ʺܵͣ���Ϊ�ظ��Ĳ������飬����ʱ���ڿ��÷�Χ :-)
 *      ���� try catch �ڰ��� return �������jad ������
 *      �еĹ��캯����ʹ�� jad -f Ҳ�ǲ�����ϰ��������±������
 */
import java.io.*;
import java.util.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.util.SyntheticRepository;

public class S {
    public static void main(String[] args) {
        SyntheticRepository repos = SyntheticRepository.getInstance();
        ClassParser parser;
        JavaClass clazz;
        JavaClass[] clazzes;
        S s = new S();
        int i;

        if (args.length == 0)
            args = getClassList(System.in);
        if (args == null || args.length == 0)
            return;

        clazzes = new JavaClass[args.length];

        // ���ڲ��Ĵ���
        for (i = 0; i < args.length; ++i) {
            System.out.println("Processing class: " + args[i]);
            try {
                parser = new ClassParser(args[i]);
                clazz = parser.parse();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            Method[] methods = clazz.getMethods();
            for (int j = 0; j < methods.length; ++j) {
                s.mergeExceptionTable(methods[j]);
            }

            clazzes[i] = clazz;
            repos.storeClass(clazz);
        }

        // �漰�����֮��Ĵ���
        renameFieldsAndMethods(clazzes);

        // dump all classes
        for (i = 0; i < args.length; ++i) {
            try {
                if (null != clazzes[i])
                    clazzes[i].dump(args[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String[] getClassList(InputStream ins) {
        String line;
        BufferedReader reader = null;
        ArrayList list = new ArrayList();
        try {
            reader = new BufferedReader(new InputStreamReader(ins));
            while (null != (line = reader.readLine()))
                list.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }

        String[] s = new String[list.size()];
        for (int i = 0; i < s.length; ++i)
            s[i] = (String)list.get(i);
        return s;
    }

    public static void renameFieldsAndMethods(JavaClass[] clazzes) {
        ConstantPool[] pools;
        ArrayList[] newpools;       /* ÿһ�� newpool ��Ӧһ�� Constant[]������̬���,
                                     * ÿ����һ�� ConstantCP��������һ�� ConstantUtf8
                                     * ��һ�� ConstantNameAndIndexType�����ߵ� name ����
                                     * ǰ�ߡ�
                                     */
        int[][] newFieldNames = new int[clazzes.length][];
        int[][] newMethodNames = new int[clazzes.length][];
        int i, j;

        System.out.println("renameFieldsAndMethods(): clazzes.length=" + clazzes.length);

        pools = new ConstantPool[clazzes.length];
        newpools = new ArrayList[clazzes.length];
        for (i = 0; i < clazzes.length; ++i) {
            if (null == clazzes[i])
                continue;

            pools[i] = clazzes[i].getConstantPool();
            newpools[i] = new ArrayList(64);
            Constant[] cp = pools[i].getConstantPool();
            for (j = 0; j < cp.length; ++j)
                newpools[i].add(cp[j]);

            newFieldNames[i] = new int[clazzes[i].getFields().length];
            newMethodNames[i] = new int[clazzes[i].getMethods().length];
            for (j = 0; j < newFieldNames[i].length; ++j)
                newFieldNames[i][j] = -1;
            for (j = 0; j < newMethodNames[i].length; ++j)
                newMethodNames[i][j] = -1;
        }

        // ��������ĳ�Ա�����ͷ����������ֺ������ "_" + i + "_" + j
        for (i = 0; i < pools.length; ++i) {
            if (null == pools[i])
                continue;
            
            // ��ĳ�Ա����
            Field[] fields = clazzes[i].getFields();
            for (j = 0; j < fields.length; ++j) {
                // �����������һ�����������ַ�
                if (fields[j].getName().length() > 2)
                    continue;
                int[] ij = findFieldOrMethod(clazzes, i,
                        fields[j].getName(), fields[j].getSignature(), true);
                renameFieldOrMethod(j, fields[j].getName() + "_" + ij[0] + "_" + ij[1] + "_f",
                        newpools[i], newFieldNames[i]);
            }

            // ��ķ���
            Method[] methods = clazzes[i].getMethods();
            for (j = 0; j < methods.length; ++j) {
                // �����������һ�����������ַ�
                if (methods[j].getName().length() > 2)
                    continue;
                int[] ij = findFieldOrMethod(clazzes, i,
                        methods[j].getName(), methods[j].getSignature(), false);
                renameFieldOrMethod(j, methods[j].getName() + "_" + ij[0] + "_" + ij[1] + "_m",
                        newpools[i], newMethodNames[i]);
            }

            // �������е� field or method
            Constant[] cp = pools[i].getConstantPool();
            for (j = 0; j < cp.length; ++j) {
                if (! (cp[j] instanceof ConstantCP))
                    continue;

                ConstantCP cfm = (ConstantCP)cp[j];
                String classname = cfm.getClass(pools[i]);
                if (classname.startsWith("java.") || classname.startsWith("javax."))
                    continue;

                ConstantNameAndType nametype = (ConstantNameAndType)cp[cfm.getNameAndTypeIndex()];
                System.out.println("got: " + clazzes[i].getClassName() + "|"+nametype.getName(pools[i]) + " " + nametype.getSignature(pools[i]));
                // �����������һ�����������ַ�
                if (nametype.getName(pools[i]).length() > 2)
                    continue;

                System.out.println("----fieldOrMethod: " + cfm);
                System.out.println("----classname=" + classname + " clazzes[i].name=" + clazzes[i].getClassName());
                int n = i;
                if (! classname.equals(clazzes[i].getClassName()))
                    n = findClassByName(clazzes, classname);

                boolean isField = cfm instanceof ConstantFieldref;
                int[] ij = findFieldOrMethod(clazzes, n,
                        nametype.getName(pools[i]), nametype.getSignature(pools[i]),
                        isField);
                System.out.println("i=" + i + " n=" + n + " ij[]: " + ij[0] + " " + ij[1]);
                if (i == ij[0]) { // ʹ�� field or method ��������Ӧ�� name index
                    renameConstantFieldOrMethod(cfm, j, nametype,
                                null /* ignored */,
                            newpools[i], isField ? newFieldNames[i][ij[1]] : newMethodNames[i][ij[1]]);
                } else {
                    renameConstantFieldOrMethod(cfm, j, nametype,
                                nametype.getName(pools[i]) + "_" + ij[0] + "_" + ij[1] + (isField ? "_f" : "_m"),
                            newpools[i], -1 /* ignored */);
                }
            }
        }

        /* �����µ� Constant[] �� ConstantPool */
        for (i = 0; i < clazzes.length; ++i) {
            System.out.println(clazzes[i].getClassName());
            if (null == clazzes[i] || newpools[i].size() == pools[i].getLength())
                continue;
            System.out.println("+++" + clazzes[i].getClassName());
            
            Constant[] c = new Constant[newpools[i].size()];
            for (j = 0; j < c.length; ++j)
                c[j] = (Constant)newpools[i].get(j);
            pools[i].setConstantPool(c);

            Field[] fields = clazzes[i].getFields();
            for (j = 0; j < fields.length; ++j)
                if (newFieldNames[i][j] > 0) {
                    System.out.println("fields[j]=" + fields[j].getName());
                    fields[j].setNameIndex(newFieldNames[i][j]);
                    System.out.println("new fields[j]=" + fields[j].getName());
                }

            Method[] methods = clazzes[i].getMethods();
            for (j = 0; j < methods.length; ++j)
                if (newMethodNames[i][j] > 0) {
                    System.out.println("methods[j]=" + methods[j].getName());
                    methods[j].setNameIndex(newMethodNames[i][j]);
                    System.out.println("new methods[j]=" + methods[j].getName());
                }
        }
    }

    public static void renameFieldOrMethod(int j, String newname, ArrayList newpool, int[] newNames) {
        ConstantUtf8 utf = new ConstantUtf8(newname);
        newpool.add(utf);
        newNames[j] = newpool.size() - 1;
    }

    public static void renameConstantFieldOrMethod(ConstantCP cfm, int j,
            ConstantNameAndType old, String newname, ArrayList newpool, int k) {
        ConstantNameAndType newc = new ConstantNameAndType(old);
        ConstantUtf8 utf;
        if (k >= 0) {
            newc.setNameIndex(k);
        } else {
            utf = new ConstantUtf8(newname);
            newpool.add(utf);
            newc.setNameIndex(newpool.size() - 1);
        }

        newpool.add(newc);
        ConstantCP newcfm = (ConstantCP)cfm.copy();
        newcfm.setNameAndTypeIndex(newpool.size() - 1);
        newpool.set(j, newcfm);
    }

    public static int findClassByName(JavaClass[] clazzes, String classname) {
        for (int i = 0; i < clazzes.length; ++i)
            if (classname.equals(clazzes[i].getClassName()))
                return i;
        
        return -1;
    }

    /* �� clazzes[n] �࿪ʼ������ĳ����Ա���������� */
    public static int[] findFieldOrMethod(JavaClass[] clazzes, int n,
            String name, String signature, boolean isField) {
        int[] ij = new int[2];
        int i, j;
        JavaClass clazz = clazzes[n];

        JavaClass[] superClazzes;
        JavaClass[] interfaces;
        try {
            superClazzes = clazz.getSuperClasses();
            interfaces = clazz.getAllInterfaces();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        for (i = superClazzes.length - 1; i >= 0; --i) {
            ij[0] = findClassByName(clazzes, superClazzes[i].getClassName());
            if (ij[0] < 0)
                continue;
            ij[1] = findFieldOrMethod(clazzes[ij[0]], name, signature, isField);
            if (ij[1] >= 0)
                return ij;
        }

        for (i = interfaces.length - 1; i >= 0; --i) {
            ij[0] = findClassByName(clazzes, interfaces[i].getClassName());
            if (ij[0] < 0)
                continue;
            ij[1] = findFieldOrMethod(clazzes[ij[0]], name, signature, isField);
            if (ij[1] >= 0)
                return ij;
        }

        ij[0] = n;
        ij[1] = findFieldOrMethod(clazz, name, signature, isField);
        if (ij[1] >= 0)
            return ij;

        throw new RuntimeException("Can't find fieldOrMethod: " + clazz.getClassName() + "|" + name + ": " + signature);
    }

    /* �� clazz ���в���ĳ����Ա */
    public static int findFieldOrMethod(JavaClass clazz, String name, String signature, boolean isField) {
        FieldOrMethod[] fm;
        if (isField)
            fm = clazz.getFields();
        else
            fm = clazz.getMethods();

        for (int j = 0; j < fm.length; ++j)
            if (name.equals(fm[j].getName()) && signature.equals(fm[j].getSignature()))
                return j;

        return -1;
    }

    /*
     * ������������ֿ����������߶��һ�����쳣�������ϲ�������
     * from     to      target      type
     * 0        55      78          55
     * 56       77      78          55
     * ���������ܺϲ�Ϊһ����
     * 0        77      78          55
     */ 

    public void mergeExceptionTable(Method m) {
        Comparator cmp = new CodeExceptionComparator();
        Code code = m.getCode();
        if (null == code)
            return;

        CodeException[] table = code.getExceptionTable();
        if (null == table || table.length < 2)
            return;

        Arrays.sort(table, cmp);

        int len = table.length;
        for (int i = 1; i < table.length; ++i) {
            for (int j = 0; j < i && j < len; ++j) {
                if (table[i].getStartPC() - 1 == table[j].getEndPC() &&
                        table[i].getHandlerPC() == table[j].getHandlerPC() &&
                        table[i].getCatchType() == table[j].getCatchType()) {
                    --len;
                    table[j].setEndPC(table[i].getEndPC());
                    break;
                }
            }
        }

        if (len == table.length)
            return;
        else {
            System.out.println("    merge exception table: " + m);
            System.out.println("    table length, old=" + table.length + " new=" + len);
        }

        CodeException[] newtable = new CodeException[len];
        System.arraycopy(table, 0, newtable, 0, len);
        code.setExceptionTable(newtable);
    }

    class CodeExceptionComparator implements Comparator {
        public CodeExceptionComparator() {
        }

        public int compare(Object o1, Object o2) {
            CodeException e1 = (CodeException)o1;
            CodeException e2 = (CodeException)o2;

            if (e1.getStartPC() < e2.getStartPC())
                return -1;
            else if (e1.getStartPC() > e2.getStartPC())
                return 1;
            else { // equal start pc
                if (e1.getEndPC() < e2.getEndPC())
                    return -1;
                else if (e1.getEndPC() > e2.getEndPC())
                    return 1;
                else  { // equal end pc
                    if (e1.getHandlerPC() < e2.getHandlerPC())
                        return -1;
                    else if (e1.getHandlerPC() > e2.getHandlerPC())
                        return 1;
                    else
                        return 0;
                }
            }
        }
    }
}

