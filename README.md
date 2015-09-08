# ms-flashlight
Automatically exported from code.google.com/p/ms-flashlight (mostly)

Spotlite is a web application for predicting co-complexed proteins from affinity purification-mass spectrometry data. It was developed using Google Web Toolkit (GWT), MySQL, and Hibernate. It should be deployed on an Apache Tomcat server.

**Installation Procedures**  
  
#Setup MySQL#  
1. Install MySQL  
2. Execute SQL scripts located in schema/ in order: create-tables.sql functions.sql insert-data.sql insertBioGRID.sql  
  
#Install Apache Tomcat Server (versions 6 and 7 have been tested)#  
  
#nstall/Compile SAINT-express#  
1. Follow instructions found at http://saint-apms.sourceforge.net/Main.html  
2. Make sure Tomcat has permission to execute SAINT-express  
	  
#Setup Development Environment#  
1. Checkout spotlite  
2. Run ant using build.xml target "eclipse-mysql" to create config files and download third-party libraries  
3. Fill it out build.properties with your specific parameters   
	
#Deploy to Tomcat#
1. Run ant using build.xml target "deploy" to compile and package as a WAR file  
2. Make sure tomcat has permission to read/write to the output directory  
3. Upload .war file located in dist/ to Tomcat.   
