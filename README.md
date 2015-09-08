# ms-flashlight
Automatically exported from code.google.com/p/ms-flashlight (mostly)

Spotlite is a web application for predicting co-complexed proteins from affinity purification-mass spectrometry data. It was developed using Google Web Toolkit (GWT), MySQL, and Hibernate. It should be deployed on an Apache Tomcat server.

**Installation Procedures**  
  
1) Setup MySQL  
	a) Install MySQL  
	b) Execute SQL scripts located in schema/  
	  i) In order: create-tables.sql functions.sql insert-data.sql insertBioGRID.sql  
  
2) Install Apache Tomcat Server (versions 6 and 7 have been tested)  
  
3) Install/Compile SAINT-express  
	a) Follow instructions found at http://saint-apms.sourceforge.net/Main.html  
	b) Make sure Tomcat has permission to execute SAINT-express  
	  
4) Setup Development Environment  
	a) Checkout spotlite  
	b) Run ant using build.xml target "eclipse-mysql" to create config files and download third-party libraries  
	c) Fill it out build.properties with your specific parameters   
	
5) Deploy to Tomcat   
	a) Run ant using build.xml target "deploy" to compile and package as a WAR file  
	b) Make sure tomcat has permission to read/write to the output directory  
	c) Upload .war file located in dist/ to Tomcat.   
