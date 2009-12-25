#!/usr/bin/perl -w
use strict;
use warnings;
use File::Basename;
use File::Find;

if (@ARGV < 1) {
    print "Usage:\n\tperl S.pl midlet_dir [output_dir]\n";
    exit -1;
}

my $destdir = $ARGV[1] || basename $ARGV[0];
die "Can't find directory $ARGV[0].\n" if ! -d $ARGV[0];
if (-e $destdir) {
    die "$destdir is not a directory" if ! -d $destdir;
    opendir D, "$destdir" || die "Can't opendir $destdir: $!\n";
    my @files = readdir D;
    closedir D;
    die "$destdir is not empty" if @files > 2;
} else {
    mkdir $destdir || die "Can't mkdir $destdir: $!\n";
}


find({wanted => \&wanted, no_chdir=>1}, $ARGV[0]);

system "xcopy", "/E", "Hello", $destdir;
system "xcopy", "/E", $ARGV[0], $destdir . "\\src";

my $appname = basename $ARGV[0];

# �滻 $destdir\.eclipseme �е� jad �ļ�����
open F, ">$destdir\\.eclipseme";
print F<<"EOF";
<?xml version="1.0" encoding="UTF-8"?>

<eclipsemeMetadata version="1.6.6" jad="$appname.jad">
   <device group="Sun Java Wireless Toolkit  2.3" name="DefaultColorPhone"/>
   <signing signProject="false"/>
</eclipsemeMetadata>
EOF
close F;

# �滻 $destdir\.project �е� name �ֶ�
open F, ">$destdir\\.project";
print F<<"EOF";
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>$appname</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>eclipseme.core.preverifier</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.jdt.core.javanature</nature>
		<nature>eclipseme.core.nature</nature>
	</natures>
</projectDescription>
EOF
close F;

print <<EOF;


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Remember to copy ``$appname.jad'' to ``$destdir'' and modify
.eclipseme and .project in it to meet your requirement.

Last, copy ``$destdir'' into your eclipse workspace.
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

EOF

##########################################
sub wanted {
    return if $_ !~ /\.class$/;

    my $javafile = $File::Find::name;
    $javafile =~ s/\.class$/\.java/;

    # �ϲ��쳣�����
    #system qw(java -classpath bcel-5.2.jar;. S), $File::Find::name;
    # ������� java ����
    system qw(jad -f -o -s .java -& -d), $File::Find::dir, $File::Find::name;
    # ɾ�� class �ļ�
    unlink $File::Find::name;
    # �� \uNNNN �ַ���ת���� GBK ����
    system qw(native2ascii -reverse), $javafile, $javafile;
    # ������ class �ļ��ᵼ�� import �����������뵱ǰ����������Ϊ�˱���
    # ���ʹ�� jad �� -f ѡ���ע�͵����е� import ��䡣
    system qw(sed -i), '/^import \(java\|javax\)\./!s/^import/\/\/import/', $javafile;

    print "---------------------------------\n\n";
}

