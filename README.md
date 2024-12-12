# OpenPayd Task

## Download zip file from github or dropbox
[Github Link](https://github.com/oguz3027/OpenPayd) => Select "Code" and "Download ZIP"

[Dropbox Link]()

## Extract the file from zip file
Go downloads and extract file


## Navigate to Your Project Directory:
For Windows:

`cd path/to/your/project`

## Compile Your Java Files:
`mvn clean compile`

## Run the tests one by one

### For search scenario:
`mvn test -Dtest=CukesRunner`

For making chrome browser headless(false), go Driver.java file and make line 31 comment line or change setHeadless(true) as setHeadless(false)

### For blog posts api test:
`mvn test -Dtest=BlogPostsApiTest`

### For department crawler test:
`mvn test -Dtest=AmazonDepartmentCrawler`