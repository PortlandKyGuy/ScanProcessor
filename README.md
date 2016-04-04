
# Scan Processor

## Purpose

I have an all-in-one printer at home (Epson WF 840). I don't like keeping paper files anymore, so I scan everything and save it in cloud-based filesystems. I have found that scanning takes a lot of my time. The actual scanning isn't time consuming, but the file renaming and file moving process is time consuming. 

My purpose for this project was to save myself time by having an automated file renaming and moving job. A lot of the all-in-one printers sold in the last few years have an option for scanning to a specific computer. Many have an option of running a script after the scan is complete. For those printers that can't run a job/script after a scan is complete, you can setup a folder monitor to run a job/script when there are files in a specific folder.

I decided to put this on github in case it helps others by saving them time or helping them learn to code.  Feel free to contribute to the project.

## Overview

This application is broken into two parts. The first part is everything related to configuration. The other part is the job that is run when a scan happens.  The default page page is index.jsp, which is a configuration page. 

####  Configuration 

This is where configuration files are created, manipulated, and saved for future use. Configuration files provide Post Scan Job with details needed to work correctly. 

Index.jsp is the main jsp page for this application. From the index page, one can change the Active Config File.

MainServlet.java is the servlet responsible for handling configuration file work. 


####  Post Scan Job

The Post Scan Job is the web service that is called after a scan has finished. It is a simple web service that just needs to be called. The flow is:

1. Load preferences. This includes the scan folder this job picks up from.
2. Load the active config file. This is the configuration file containing the destination directory and destination filename pattern.
3. Loop through each file in the scan pickup directory.
    1. Generate a new filename.
    2. Generate the destination path with the new filename and destination directory.
    3. Move the file.
   
The ScanProcessor.java servlet is responsible for Post Scan Job behaviors. To kick off the Post Scan Job, call <your running web service home url>/run .

#### Libs Used
This project uses Apache's commons io, Google's gson, and Google's json simple.  

## Setup

I use a Mac, so these instructions are for Mac. It shouldn't be that difficult to modify for Windows or other OS.

I use Apache Tomcat to run this application locally. The printer/scanner I have only allows an Application to be run after a scan, not a shell script or call to a web service. So, I used Automator to create an App. In Automator, it was as simple as File -> New -> Application. Then drag over a Run Shell Script action. Then add a curl command like:

    curl -X GET -i http://localhost:8080/ScanProcessorWeb/run

Once I saved the Automator app, I was able to have the printer call this App after a scan. The App calls my  web service. The scanned files are processed. I am happy.  
 
## Commong Terms
 
Active Config File: The configuration file that the Post Scan Job will use to determine what to do with the scanned files.
 
Config File: One of many pre-configured configuration files that can become the Active Config File. This is a time saver for the end user.
 


