�ļ���;
--------
Hello   EcpliseME ��ģ�幤��
S.java  Ԥ���� Java �࣬ȥ���������ɣ����� jad ˳��������
S.pl    ���� jad��native2ascii������ Eclipse ���̵�
bcel-5.2.jar    �ֽ��봦��⣬apache.org ��Ʒ
jad.exe         ��õ� Java �����빤�ߣ���ϧû��Դ��
Sword&Rose_X859.jar/jad     ʥ��õ��ԭʼӦ��
SwordRose_X859-ecplise.rar  ��������ʥ��õ�� Eclipse ����

ʹ��ʾ��
--------
��ѹ�� Sword&Rose_X859.jar �� Sword&Rose_X859;
javac -classpath bcel-5.2.jar S.java
cd Sword&Rose_X859
dir /s/b *.class | java -classpath c:\WTK22\lib\cldcapi11.jar;c:\WTK22\lib\midpapi20.jar;..\bcel-5.2.jar;.. S 
cd ..
perl S.pl "Sword&Rose_X859" output

output ����һ�� Eclipse ���̣���Ҫ�޸����е� .eclipseme
�� .project �� Sword&Rose_X859 �еġ�&��ȥ����


��������
--------
ʥ��õ����Ҫ�ֹ������ĵط���
1. ĳЩ���ù��캯���ĵط�û��дȫ���������ڱ�������ʱ������ȫ������
���Ժ�������������
2. try catch �ڰ��� return ʱ jad �޷���ȷ��������취���� 
javap -c ClassName ���������������ͷ���쳣����� try catch�������ס�
3. e.class ��ͷһ���������� getstatic/pop ���У�����ǰһ�� getstatic
��������ģ�jad ʶ�𲻳���������������� javap -c ClassName �Ľ����
���Իָ�������

