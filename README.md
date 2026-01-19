# straglr-tsv2vcf
Java tool to create a vcf from the straglr tsv output

## Usage
usage: java -jar genemap-mapper.jar -i <arg> -b <arg> -r <arg> [-h <arg>]
       -s <arg> [-o <arg>] [-f]
 -i,--input <arg>             Straglr tsv file.
 -b,--bed <arg>               bed catalog file (.bed).
 -r,--reference <arg>         Reference sequence file (.fna).
 -h,--haploid_contigs <arg>   Comma separated list of haploid contigs.
 -s,--sample <arg>            Sample name to be used in the VCF.
 -o,--output <arg>            Output file (.tsv).
 -f,--force                   Override the output file if it already
                              exists.

usage: java -jar genemap-mapper.jar -v
 -v,--version   Print version.
