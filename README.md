# MS-Spotlite
Automatically exported from code.google.com/p/ms-flashlight (mostly)

Spotlite is a web application for predicting co-complexed proteins from affinity purification-mass spectrometry data. It was developed using Google Web Toolkit (GWT), MySQL, and Hibernate. It should be deployed on an Apache Tomcat server.
***
# Installation Procedures   
  
**Setup MySQL**  
1. Install MySQL  
2. Execute SQL scripts located in schema/ in order: create-tables.sql functions.sql insert-data.sql insertBioGRID.sql  
  
**Install Apache Tomcat Server** (versions 6 and 7 have been tested)  
  
**Install/Compile SAINT-express**  
1. Follow instructions found at http://saint-apms.sourceforge.net/Main.html  
2. Make sure Tomcat has permission to execute SAINT-express  
	  
**Setup Development Environment**  
1. Checkout spotlite project in Eclipse
2. Run ant using build.xml target "eclipse-mysql" to create config files and download third-party libraries  
3. Fill it out build.properties with your specific parameters   
	
**Deploy to Tomcat**  
1. Run ant using build.xml target "deploy" to compile and package as a WAR file  
2. Make sure tomcat has permission to read/write to the output directory  
3. Upload .war file located in dist/ to Tomcat.   
***

# Getting Started - Developers
#### Technologies and frameworks  
* ***Google Web Toolkit (GWT)***  - This is the main framework used for the project. GWT allows us to build an entire web application in Java - leverging object oriented programming using a single language for both the front-end and back-end. The GWT compiler translates the front-end Java into javascript. Learn more here: http://www.gwtproject.org/
* ***MySQL*** - The relational database used to store data. The data includes the precomputed features for all human gene-gene pairs necessary for classification of true/false interactions, protein sequences from UniProtKB and IPI, Gene information from NCBI, GO annotations, and ID mapping information.
* ***Hibernate***  - This is a framework for Object Relational Mapping (ORM). ORM allows us to automatically convert the results of an SQL query into an object of the appropriate class. Typically, each table in the database has its own class. For example, the query "select * from proteins" will return a List of Protein objects. All the programmer has to do is define the mapping of columns to fields in the Protein class. For general cases, this makes the programmer's life easy, for some special purpose queries, this sometimes makes things difficult.
* ***C3P0***  - The purpose of this framework is to manage database connections. Establishing a new connect is expensive in terms of time. Therefore C3P0 performs connection pooling and reuses old connections whenever possible. Using this approach, we typically don't have to create a new connection for each new visitor to the website. If an old one is free, it is reused. C3P0 takes care of it all behind the scenes.
* ***Apache Tomcat***  - Since the back-end is a java servlet, we host the web application on an Apache Tomcat server. This program handles serving requests to our website, writes logs, etc.
* ***Eclipse*** - Although it's not necessary to use Eclipse as your IDE, it's what I used throughout development. I'm pretty sure the build scripts do not depend on it. I've setup and deployed the entire project via command line. However, I think I do write a preferences file that's Eclipse specific to setup class-paths and files to ignore.

#### Directory structure  

**schema/** Contains SQL files to create tables, drop tables, and insert data into MySQL  
**war/** Contains resources necessary for deployment: images, javascript, CSS, HTML, and web.xml  
**lib-local/**  Contains some third-party libraries that are not available through online repositories. The rest are downloaded when executing the ANT build script.

**src/**  
**----> edu.unc.flashlight.client/** application entry point, constants, messages, navigation history  
**----------> command/** extended wrappers for RPC calls  
**----------> resource/** manages image and js resources   
**----------------> css/** manages css resources  
**----------> service/** interfaces for communication with the server   
**----------> ui/** Contains menu class  
**----------------> activity/** Implementation of "presenters"  
**----------------> event/** Events that should trigger a response (e.g. file upload complete)  
**----------------> place/** Definitions of "web pages"  
**----------------> validation/** validation messages  
**----------------> view/** Interfaces for a web page  
**----------------------> impl/**  Actual web pages  
**----------------> widget/** Customized, reusable widgets  
**----------------------> details/** widgets for details of features used in classification  
**----------------------> js/** widgets that needed to be used via javascript because no GWT wrapper existed for them.  
**----------------------> popup/** popup widgets  

**----> edu.unc.flashlight.shared/** code that both the server and client use  
**----------> exception/** exceptions...  
**----------------> upload/** exceptions specific for the upload process  
**----------> model/** classes for data transferred between client and server. Contains Hibernate mappings.   
**----------------> details/** specifically for details of features used for classification  
**----------------> SAINT/** specifically for SAINT parameters  
**----------------> table/** specifically for information needed to display results in a table (# of total results, which page of the table we're displaying, etc)  
**----------------> upload/** specifically for data related to the upload process  
**----------> util/** Converters, constants, random support  
**----------> validation/** Input validation classes (e.g. constraints defined by the database schema)  

**----> edu.unc.flashlight.server/** Servlets that handle all RPC communication  
**----------> dao/** Data Access Objects. Gets results from SQL queries, defines SQL queries that don't fit into a particular class.  
**----------> graph/** Graph theory stuff  
**----------> hibernate/** Old hibernate mappings. Deprecated except for a couple cases. Hibernate.cfg.xml shows which classes are still linked to files here.   
**----------> ms/** APMS scoring algorithms  
**----------> parser/** Uploaded data parsers  
**----------> rf/** Machine learning classes. Used to be Random Forest hence rf  
**----------> util/**  
**----------------> schedule/** Anything on a timer. Handles deleting data after 24 hours.  

#### Configuration files  

* **web.xml** (located in war/WEB-INF/) Defines servlets. Every RPC goes through a servlet and each servlet needs a URL and a mapping to a class.
* **log4j.properties** (located in src/) properties for logging. Verbose? Errors only? Warnings?
* **build.properties** (located in root directory, after executing build.xml ANT script) Contains DB usernames, passwords, directory location for temporary uploaded data.
* **hibernate.cfg.in.xml** (located in root directory) Initial config file for hibernate. Is filled in with values from build.properties and written to src/. It specifies which classes are mapped to tables in the database, database connection properties, and C3P0 connection pooling properties.
* **Flashlight.gwt.xml** (located in src/edu.unc.flashlight/) Configuration for GWT. It's sometimes necessary to include information about third-party libraries used on the client side.
* **ivy.xml** (located in root directory) Defines third-party libraries to be downloaded. This keeps us from having to store a bunch of big files in version control. Also allows us to get newer versions of the libraries.
* **build.xml** (located in root directory) This is the ANT script with all the build targets. Use it to setup the project. It downloads third-party libraries, writes config files, compiles and deploys the project into a WAR file. The WAR is uploaded to Tomcat for deployment.

#### Design patterns 

* Individual webpages are designed using the Model-View-Presenter design pattern. Read more about using MVP with GWT here: http://www.gwtproject.org/articles/mvp-architecture.html
* All communication between client and server is accomplished through Remote Procedure Calls (RPC). These are asynchronous, allowing the web application to remain responsive while waiting for results from the server. More information can be found here: http://www.gwtproject.org/doc/latest/tutorial/RPC.html
and here: http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html
