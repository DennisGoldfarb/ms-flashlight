# MS-Spotlite
Automatically exported from code.google.com/p/ms-flashlight (mostly)

Spotlite is a web application for predicting co-complexed proteins from affinity purification-mass spectrometry data. It was developed using Google Web Toolkit (GWT), MySQL, and Hibernate. It should be deployed on an Apache Tomcat server. Publication: http://pubs.acs.org/doi/abs/10.1021/pr5008416
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

Begin by following installation procedures to setup the project in Eclipse. Use the figure below and the directory structure breakdown further down to familiarize yourself with how the code is organized. Information on the necessary steps to achieve common development tasks are found at the end.  

![Image of folder structure] (https://github.com/DennisGoldfarb/ms-flashlight/blob/master/GettingStarted.png)

#### Technologies and frameworks  
* ***Google Web Toolkit (GWT)***  - This is the main framework used for the project. GWT allows us to build an entire web application in Java - leverging object oriented programming using a single language for both the front-end and back-end. The GWT compiler translates the front-end Java into javascript. Definitely devour the information found in http://www.gwtproject.org/ if you've never used GWT before. 
* ***MySQL*** - The relational database used to store data. The data includes the precomputed features for all human gene-gene pairs necessary for classification of true/false interactions, protein sequences from UniProtKB and IPI, Gene information from NCBI, GO annotations, and ID mapping information.
* ***Hibernate***  - This is a framework for Object Relational Mapping (ORM). ORM allows us to automatically convert the results of an SQL query into an object of the appropriate class. Typically, each table in the database has its own class. For example, the query "select * from proteins" will return a List of Protein objects. All the programmer has to do is define the mapping of columns to fields in the Protein class. For general cases, this makes the programmer's life easy, for some special purpose queries, this sometimes makes things difficult.
* ***C3P0***  - The purpose of this framework is to manage database connections. Establishing a new connect is expensive in terms of time. Therefore C3P0 performs connection pooling and reuses old connections whenever possible. Using this approach, we typically don't have to create a new connection for each new visitor to the website. If an old one is free, it is reused. C3P0 takes care of it all behind the scenes.
* ***Apache Tomcat***  - Since the back-end is a java servlet, we host the web application on an Apache Tomcat server. This program handles serving requests to our website, writes logs, etc.
* ***Eclipse*** - Although it's not necessary to use Eclipse as your IDE, it's what I used throughout development. I'm pretty sure the build scripts do not depend on it. I've setup and deployed the entire project via command line. However, I think I do write a preferences file that's Eclipse specific to setup class-paths and files to ignore.

#### Basic workflow  
The purpose of the application is to get a file from the user and analyze it. The process goes like this:  
1. A user selects a file and some parameters for analysis. (client side)  
2. The file is uploaded to a temp directory on the server. (client/server side)  
3. The file is parsed. (server side)  
4. The identifiers of the file are mapped to Entrez Gene IDs. (server side)  
5. The APMS data is inserted into our database. (server side)  
6. The APMS data is scored via the selected APMS scoring algorithm. Permutations are done to calculate P-values. (server side)  
7. If the user requested the use of "indirect data" to help score their data, then those feature scores are retrieved from our database. (server side)  
8. The APMS data is further scored via a logistic regression model using the previously retrieved indirect data. (server side)
9. The results are written to the database. (server side)  
10. The results are returned to the user. (client/server side)  

#### Directory structure  

**data/** Contains the TIP49 dataset as an example file to upload  
**schema/** Contains SQL files to create tables, drop tables, and insert data into MySQL  
**war/** Contains resources necessary for deployment: images, javascript, CSS, HTML, and web.xml  
**lib-local/**  Contains some third-party libraries that are not available through online repositories. The rest are downloaded when executing the ANT build script.

**src/**  
**----> edu.unc.flashlight.client/** All client side code. Application entry point, constants, messages, navigation history  
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

**----> edu.unc.flashlight.shared/** code that both the server and client use. Mostly defines classes for data that needs to be passed between client and server.  
**----------> exception/** exceptions...  
**----------------> upload/** exceptions specific for the upload process  
**----------> model/** classes for data transferred between client and server. Contains Hibernate mappings. Contains SQL/HQL queries  
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

#### Adding a new web page  

* Create a new Place class for your new page in src/edu/unc/flashlight/client/ui/place/
* Create a new Activity class for your new page in src/edu/unc/flashlight/client/ui/activity/
* Create a new View interface in src/edu/unc/flashlight/client/ui/view/
* Create a new ViewImpl class (defines the functions behind the UI) in src/edu/unc/flashlight/client/ui/view/impl
* Create a new .ui.xml file to describe the UI (pretty much html) in src/edu/unc/flashlight/client/ui/view/impl
* Add any necessary text to the FlashlightConstants interface in src/edu/unc/flashlight/client/
* Add your new Place and Activity to the getActivity function in AppActivityMapper in src/edu/unc/flashlight/client/
* Add your Place's Tokenizer to the AppPlaceHistoryMapper class in src/edu/unc/flashlight/client/
* Add your Place to the PlaceFactory class in src/edu/unc/flashlight/client/
* Add a new menu link to the FlashlightMenu class in src/edu/unc/flashlight/client/ui/
* Add your View to the ClientFactory interface in src/edu/unc/flashlight/client/
* Add your View to the ClientFactoryImpl class in src/edu/unc/flashlight/client/

#### Creating a new function call between client and server

* Add your function to the appropriate Service and AsyncService interfaces found in src/edu/unc/flashlight/client/service/
* Implement your function in the appropriate ServletImpl class found in /src/edu/unc/flashlight/server/

#### Creating a new servlet

* Create a new ServletImpl class in /src/edu/unc/flashlight/server/
* Create an AsyncService interface in src/edu/unc/flashlight/client/service/
* Create a Service interface in src/edu/unc/flashlight/client/service/
* Add the AsyncService to the Flashlight class
* Add your Servlet information to web.xml in war/WEB-INF/

#### Changing the database schema

* Modify the sql files found in schema/
* Modify/Add a Model class that describes your table in src/edu/unc/flashlight/shared/model/
* Add modify and queries in the Model class
* Modify/Add the corresponding DAO class in src/edu/unc/flashlight/server/dao/
* If a new class/model, add it to hibernate.cfg.in.xml in the root directory
