@echo off

@echo clean...
make -f Make_ming.mak clean DEBUG=yes

@echo make...
make -f Make_ming.mak all GUI=yes OLE=yes MBYTE=yes IME=yes GDYNAMIC_IME=yes GIME=yes PERL=d:/work/program/Perl DYNAMIC_PERL=yes PERL_VER=58 PYTHON=d:/work/program/Python24 DYNAMIC_PYTHON=yes PYTHON_VER=24 CSCOPE=yes CPUNU=i586 DEBUG=yes MAP=lines

@echo ������strip.exeɾȥ������exe�ļ��еĵ��Է����Լ�С��ִ���ļ������
@echo done.

pause

