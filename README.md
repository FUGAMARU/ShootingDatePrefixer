# ShootingDatePrefixer

## Overview

Retrieves metadata (EXIF, etc.) of image and video files in the specified directory and adds the shooting date as a
prefix to the file name. (as in `2023_03_05_`)  
HEIC or HEIF formats, commonly found on Apple devices, are also supported.

## How to Use

1. Download the latest version of the jar file from
   the [Releases page](https://github.com/FUGAMARU/ShootingDatePrefixer/releases)
1. Run the jar file (Java 17 or higher is required)  
   `java -jar ShootingDatePrefixer.jar "/path/to/dir"`

## Arguments & Options

The argument is the directory path to be processed (processing is also performed on subdirectories).  
Example: `java -jar ShootingDatePrefixer.jar "/path/to/dir"`

If the date and time of the media could not be successfully retrieved, the processing of that file will be skipped. (
e.g., if text files are mixed in the directory).

`-mod` option, if valid shooting date and time data cannot be obtained from the metadata, the file modification date and
time will be used as a prefix instead.  
Example: `java -jar ShootingDatePrefixer.jar -mod "/path/to/dir"`

## License

This project is released under the MIT License, see LICENSE file.