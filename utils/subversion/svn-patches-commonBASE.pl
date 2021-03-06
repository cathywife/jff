#!/usr/bin/perl -w
use strict;
use warnings;
use File::Copy;
use File::Spec;

my $diffsDIR = shift;
my $commonBASE = shift;

die "Usage: perl $0 diffsDIR [commonBASE]\n" if !defined $diffsDIR;

my $LANG=$ENV{LANG};
$ENV{LANG} = "en_US.UTF-8";     # make parsing easy
my $svncmd = "svn";
if (exists $ENV{SVNUSER} && exists $ENV{SVNPASSWD}) {
    $svncmd .= " --username $ENV{SVNUSER} --password $ENV{SVNPASSWD} ";
}

######################################################
# check whether svn has been installed
#
if (! qx/svn --version/) {
    die "[svn-patches] Can't execute 'svn': $!\n";
}

######################################################
# check whether patch has been installer
#
if (! qx/patch --version/) {
    die "[svn-patches] Can't execute 'patch': $!\n";
}

######################################################
# check whether we are in a clean working copy
#
die "[svn-patches] Not a clean working copy.\n" if `svn status`;

######################################################
# get server URL and set default commonBASE
#
my ($serverURL, $line, @lines);
open F, "$svncmd info |" || die "[svn-patches] Can't execute 'svn info': $!\n";
@lines = <F>;
close F;

($line) = grep /^URL:/, @lines;
($line) = $line =~ /^URL: (.*)/;
$serverURL = $line;

($line) = grep /^Last Changed Rev:/, @lines;
($line) = $line =~ /(\d+)/;
$commonBASE = $line if (!defined $commonBASE) || ($commonBASE > $line);

######################################################
# retrieve last applied diff
#
my $svnadminDIR = ".svn";
$svnadminDIR = "_svn" if exists $ENV{SVN_ASP_DOT_NET_HACK};

my $prevDIFF;
my $prevDIFF_file = File::Spec->catfile($svnadminDIR, "_prevDIFF_");
if (-e $prevDIFF_file) {
    open F, "<$prevDIFF_file" || die "[svn-patches] Can't open $prevDIFF_file: $!\n";
    $prevDIFF = <F>;
    chomp $prevDIFF;
    close F;
}

######################################################
# apply diffs as many as possible or only one
#
my @diffs = glob(File::Spec->catfile($diffsDIR, "*.diff"));
die "[svn-patches] No .diff found in $diffsDIR.\n" if @diffs < 1;

@diffs = sort {
    my ($a1) = $a =~ /\d+-\d+-(\d+)\.diff$/;
    my ($b1) = $b =~ /\d+-\d+-(\d+)\.diff$/;
    $a1 <=> $b1;
} @diffs;

my $nextDIFF;       # diff being applied
if (!defined $prevDIFF) {
    $nextDIFF = 0;
} else {
    for ($nextDIFF = 0; $nextDIFF < @diffs; ++$nextDIFF) {
        last if $diffs[$nextDIFF] eq $prevDIFF;
    }
    if ($nextDIFF == @diffs) {
        print "[svn-patches] Can't find previous diff '$prevDIFF', can't go on patching\n";
        exit -1;
    } elsif ($nextDIFF == @diffs - 1) {
        print "[svn-patches] All patches have been applied, great!\n";
        unlink $prevDIFF_file;
        exit 0;
    } else {
        ++$nextDIFF;
    }
}

my $ret = 0;
while ($nextDIFF < @diffs) {
    print "------------------------------------------------------\n";
    print "[svn-patches] Applying '$diffs[$nextDIFF]'...\n";
    $ret = system "patch -N -p0 <" . $diffs[$nextDIFF];
    if ($ret < 0) {
        print "[svn-patches] Error happened when executed 'patch -p0': $!\n";
        print "[svn-patches] run me again after you have fixed the problem.\n";
        last;
    } elsif ($ret > 0) {     # conflict
        $prevDIFF = $diffs[$nextDIFF];
        copy_binary_files();
        save_state();
        print "[svn-patches] run me again after you have resolved conflicts.\n";
        last;
    } else {
        $prevDIFF = $diffs[$nextDIFF];
        copy_binary_files();
        save_state();
        ++$nextDIFF;
        print "[svn-patches] Successfully applied, great!\n";
        # print "[svn-patches] Try to commit it...\n";
            print "[svn-patches] run me again after you have checked and committed these changes.\n";
            last;
    }
}

######################################################
#
sub save_state {
    print "[svn-patches] Save applying state to $prevDIFF_file.\n";
    open F, ">$prevDIFF_file" || die "[svn-patches] Can't write $prevDIFF_file: $!\n";
    print F "$prevDIFF\n";
    close F;
}

######################################################
#
sub copy_binary_files {
    my $idx = $prevDIFF;
    $idx =~ s/\.diff$/\.idx/;

    if (-e $idx) {
        open F, "<$idx" || die "[svn-patches] Can't read $idx: $!\n";
        my (@files, $prefix, $f);
        @files = <F>;
        chomp @files;
        $prefix = $idx;
        $prefix =~ s/\.idx$//;
        for (my $i = 0; $i < @files; ++$i) {
            $f = "$prefix-$i.bin";
            print "[svn-patches] Copy $f to $files[$i].\n";
            copy $f, $files[$i] || die "[svn-patches] Can't copy '$f' to '$files[$i]': $!\n";
        }
        close F;
    }
}

