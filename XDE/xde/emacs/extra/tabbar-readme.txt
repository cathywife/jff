enhanced by zslevin at newsmth:

http://www.newsmth.net/bbstcon.php?board=Emacs&gid=49730

������: zslevin (Levin Du), ����: Emacs
��  ��: ���˸��Լ����ñȽ�˳�ֵ� tabbar����ӭ�������
����վ: ˮľ���� (Mon Jan  8 17:37:54 2007), ת��

���ӵĹ����У�
 1. �� tab �ϰ��м���ر� buffer�����Ҽ��򵯳��л��˵��������ڵ�ǰ tabgroup
������ tabgroup ���л���

 2. �����ƶ� tab ���ܡ���ԭ�� scroll ��ť�Ļ����ϼ�������м����Ҽ��Ĵ���
���磬�� ">" �ϰ��Ҽ����Ὣ tab ���ƣ����м����Ƶ����Ҵ���

 3. ���ӿ�ݼ����л� buffer�������� 0~9, a~z ��Щ��ݼ������ٵ��л� buffer��

�� 3 ���Ĭ���ǹرյģ�Ҫ�򿪵Ļ����ο���������ã�

    (require 'tabbar)
    (setq tabbar-speedkey-use t)
    (setq tabbar-speedkey-prefix (kbd "<f1>"))
    (tabbar-mode 1)

�������� tab �оͻ�����ݼ���ʾ���� "3'tabbar.el"����ʱ����ͨ�� <F1> 3 ��
�л����� buffer��

 4. ���� M-x tabbar-goto-group������Զ�����������л���ĳ tab group��


�Ľ��Ĺ�����(��ǰ����һ���汾��)��
 1. ��ѡ�е� tab ʼ�տɼ���

ϣ���Դ������������
--
һ����ã��������

�� �޸�:��zslevin �� Jan  8 17:39:06 �޸ı��ġ�[FROM: 61.234.125.31]
�� ��Դ:��ˮľ���� http://newsmth.net��[FROM: 61.234.125.31]

