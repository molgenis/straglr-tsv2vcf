# straglr-tsv2vcf
Java tool to create a vcf from the [straglr](https://github.com/bcgsc/straglr) tsv output.
This tool creates a VCF 4.2 file (for htsjdk support in downstream tools) in a format that can be annotated with [stranger](https://github.com/Clinical-Genomics/stranger).
The VCF output of the straglr tool itself is VCF 4.5 that cannot be used downstream in VIP and the content is not suitable for Stranger.
Since the catalog bed file is required to create the VCF this tool is only suitable for usage with output of straglr in the "targetted" mode, and not for the "genome scan" mode.

## Usage
``` 
usage: java -jar straglrTsv2Vcf.jar -i <arg> -b <arg> -r <arg> [-c <arg>]
       -s <arg> [-o <arg>] [-f]
 -i,--input <arg>             Straglr tsv file.
 -l,--loci <arg>              Loci (catalog) file used for straglr run (.tsv).
 -r,--reference <arg>         Reference sequence file (.fna).
 -c,--haploid_contigs <arg>   Comma separated list of haploid contigs.
 -s,--sample <arg>            Sample name to be used in the VCF.
 -o,--output <arg>            Output file (.tsv).
 -f,--force                   Override the output file if it already
                              exists.

usage: java -jar straglrTsv2Vcf.jar -v
 -v,--version   Print version.

usage: java -jar straglrTsv2Vcf.jar -h
 -h,--help   Print usage.
 ```

## Loci input

The loci file should contain these columns, in this order:
1) chrom: contig identifier
2) start: 0 based start position
3) stop: 0-based stop position
4) straglrRu: the repeat unit provided to straglr
5) gene: the gene of the locus
6) locusId: the identifier of the locus
7) repeatUnit: the repeat unit to match with if "-" was provided to straglr

## Example output
```
##fileformat=VCFv4.2
##ALT=<ID=STR17,Description="Short tandem repeat (STR) allele of length 17.">
##ALT=<ID=STR20,Description="Short tandem repeat (STR) allele of length 20.">
##ALT=<ID=STR9,Description="Short tandem repeat (STR) allele of length 9.">
##FILTER=<ID=PASS,Description="PASS">
##FORMAT=<ID=AD,Number=.,Type=Integer,Description="Allelic depths">
##FORMAT=<ID=DP,Number=1,Type=Integer,Description="Read depth">
##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##INFO=<ID=END,Number=1,Type=Integer,Description="End position">
##INFO=<ID=REPID,Number=1,Type=String,Description="Locus identifier.">
##INFO=<ID=RU_CALL,Number=1,Type=String,Description="Most frequent actual repeat motif">
##INFO=<ID=RU_CAT,Number=1,Type=String,Description="Catalog repeat motif">
##INFO=<ID=RU_MATCH,Number=0,Type=Flag,Description="RU call matches catalog (allowing shift/IUPAC)">
##INFO=<ID=RU_SEEN,Number=.,Type=String,Description="All RUs encountered with read counts">
##contig=<ID=1,length=101>
##contig=<ID=2,length=101>
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	SAMPLE
1	2	.	T	<STR9>,<STR20>	.	PASS	END=11;REPID=AR;RU_CALL=GGC;RU_CAT=GCA;RU_SEEN=GGC(4)	GT:AD:DP	1/2:2,2:4
2	2	.	T	<STR17>	.	PASS	END=12;REPID=ATN1;RU_CALL=CAG;RU_CAT=CNG;RU_MATCH;RU_SEEN=CAG(2)	GT:AD:DP	1/1:2:2
```