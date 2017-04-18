# seam
Using seam carving technique to resize picture.

In addition to standard seam carving, I also convert a picture into 10 colums like grid system in twitter Bootstrap, you can choose to proccess each colum and leave other part untouched.
For example, the following code means that reduce image width to `25%` and the seam is searched from the first 3 colums. 
```
Picture picture = new Picture(args[0]);

SeamCarver seamCarver = new SeamCarver(picture);

for (int i = 0; i < seamCarver.width() / 4; i++) {
    seamCarver.CarvingOnce(0, 3);
}

Picture newPic = seamCarver.getPicture();
newPic.show();
```       
#### GUI using Java Swing 

![alt text](https://github.com/HoweZZH/SeamCarving/blob/master/guiPicture/1gui.png?raw=true "gui")

Original Picutre
![alt text](https://github.com/HoweZZH/SeamCarving/blob/master/testPicture/1.jpg?raw=true "original picture")

after process
![alt text](https://github.com/HoweZZH/SeamCarving/blob/master/testPicture/processed.jpg?raw=true "original picture")

convert calculated Energy 2d color array into greyscale picture
![alt text](https://github.com/HoweZZH/SeamCarving/blob/master/testPicture/OriginalEnergyGreyScale.jpg?raw=true "original picture")

after process
![alt text](https://github.com/HoweZZH/SeamCarving/blob/master/testPicture/processedEnergyGreyScale.jpg?raw=true "original picture")
