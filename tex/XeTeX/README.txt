(1) LaTeX + CJK
from
http://bj.soulinfo.com/~hugang/tex/tex2007/YueWang-zhfonts-final_1.01.tar.bz2

http://bbs.ctex.org/viewthread.php?tid=45752

cd ~/
mv .texlive2008 .texlive2008_backup
tar -jxf YueWang-zhfonts-final_1.01.tar.bz2
mv .texlive2007 .texlive2008
# ��Ҫ���²������е�map�ļ�����Ϊ������е���TL07��map
rm -rf .texlive2008/texmf-var/web2c
updmap

�������Ĭ������ simsun һ�����壬���÷�����������������Ҫ�޸�
.texlive2008\texmf-var\fonts\map\dvipdfm\cid-x.map������ֱ����
Դ�ļ���ָ�����塣
==========================================
(2) XeLaTeX + xeCJK

�� xecjk-2.2.9.zip ��ѹ�ŵ� ~/.texlive2008/texmf-var/ ����
ִ�� texhash
��������뱣��Ϊ test.tex

% # -*- coding: utf-8 -*-
\documentclass[11pt]{article}
\usepackage{xeCJK}         
\setCJKmainfont{SimSun}         
                    
%\setCJKmainfont[BoldFont=Adobe Heiti Std,ItalicFont=Adobe Kaiti %Std]{Adobe Song Std}
%\setCJKsansfont{Adobe Heiti Std}         
%\setCJKmonofont{Adobe Fangsong Std}  

\begin{document}
����
\end{document}

Ȼ��ִ��:
xelatex test.tex
=====================================
PATH=/usr/local/texlive/2008/bin/i386-linux:$PATH; export PATH
MANPATH=/usr/local/texlive/2008/texmf/doc/man:$MANPATH; export MANPATH
INFOPATH=/usr/local/texlive/2008/texmf/doc/info:$INFOPATH; export INFOPATH

