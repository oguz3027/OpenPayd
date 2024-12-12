#OpenPayd Task

##Navigate to Your Project Directory:
cd path/to/your/project


##Compile Your Java Files:
mvn clean compile


##Run the tests one by one

###For search scenario:
mvn test -Dtest=runners.CukesRunner

###For blog posts api test:
mvn test -Dtest=apiTest.BlogPostsApiTest

###For department crawler test:
mvn test -Dtest=crawlerTest.AmazonDepartmentCrawler