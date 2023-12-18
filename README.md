# comsc76-project1
Programming Project

Part I: Write a program to compress a source file into a target file using the Huffman coding method. First, use ObjectOutputStream to output the Huffman codes into the target file, then use BitOutputStream from Assignment #2 to output the encoded binary contents to the target file. Pass the files from the command line using something like the following command:

    C:\Users\.... > java Compress_a_File sourceFile.txt compressedFile.txt

When I did this for my source file, I got a compressed file that looked something like the following:

¬í w Zur [Ljava.lang.String;­ÒVçé{G xp ppppppppppt 01101ppt 11010ppppppppppppppppppt 100pppppppppppt 10101pt 1111010ppppppppppppppppppt 011000ppppppppppppppppt 1010011pt 1111011pt 1101110ppppppppppt 110110t 1101111t 00000t 01111t 010t 101000pt 00001t 11111ppt 01110t 00010t 00011t 1110t 111100pt 1011t 001t 1100t 011001pppt 1010010ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppw û§Åµ©iõÓwœ°Í­MîeWM÷t¦tËDÌÌ£îUÓXæ=…å¢gògw 

 

Part II: Write a second program, Decompress_a_File.java, to decompress a previously compressed file so that it replicates the original source file in Part I above. You would do this at the command line with a command that looks like the following:

    C:\Users\...> java Decompress_a_File compressedFile.txt decompressedFile.txt

When I ran this second program on my compressed file from Part I above I got:

Roses are red,
Violets are blue,
Try to compress this file,
And then decompress it too.

All groups will consist of 4-5 students, who will be assigned to their groups by the instructor. Groups will be further broken into subgroups, i.e., 2-3 students will be responsible for Part I to compress a source file, and 2 students will assume responsibility for Part II of the project to decompress a compressed file. Both subgroups will be responsible for ensuring that both parts are in synch.

Group Project will account for 10% (50 points) of the student’s total grade. A rubric for project grading will be posted with this assignment.
