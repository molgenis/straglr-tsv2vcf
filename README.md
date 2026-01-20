# straglr-tsv2vcf
Java tool to create a vcf from the straglr tsv output

## Usage
``` 
usage: java -jar genemap-mapper.jar -i <arg> -b <arg> -r <arg> [-h <arg>]
       -s <arg> [-o <arg>] [-f]
 -i,--input <arg>             Straglr tsv file.
 -b,--bed <arg>               straglr catalog bed file (.bed)*.
 -r,--reference <arg>         Reference sequence file (.fna).
 -h,--haploid_contigs <arg>   Comma separated list of haploid contigs.
 -s,--sample <arg>            Sample name to be used in the VCF.
 -o,--output <arg>            Output file (.tsv).
 -f,--force                   Override the output file if it already
                              exists.

usage: java -jar genemap-mapper.jar -v
 -v,--version   Print version.
 
 *: the straglr bed file can have an optional 7th column containing the desired repeat unit. This column can be used is the 4th column contains a '-' indicating that any repeat unit should be called.
```