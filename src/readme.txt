-- Database creations 
 
1) create the databases staging_area and dissemination_area with template 'template_postgis' on postgres
2) run the following command: psql -f stagingarea.sql -U postgres -d staging_area on the staging area machine
3) run the following command: psql -f disseminationarea.sql -U postgres -d dissemination_area on the dissemination area machine

--geostore setting

4) set the file geostore_datasource_ovr.properties parameters for the staging_area according to the geostore settings of the staging_area
geostoreDataSource.driverClassName=org.postgresql.Driver

geostoreDataSource.url=jdbc:postgresql://<host_name>:<port>/staging_area
geostoreDataSource.username=geostore_user_sa
geostoreDataSource.password=geostore_pwd_sa
geostoreEntityManagerFactory.jpaPropertyMap[hibernate.default_schema]=geostore

(in case you want to automatically create the geostore table set geostoreEntityManagerFactory.jpaPropertyMap[hibernate.hbm2ddl.auto]=update
then reset it to validate after the first execution)

5) set the file geostore_datasource_ovr.properties parameters for the dissemination_area according to the geostore settings of the dissemination_area
geostoreDataSource.driverClassName=org.postgresql.Driver

geostoreDataSource.url=jdbc:postgresql://<host_name>:<port>/dissemination_area
geostoreDataSource.username=geostore_user_da
geostoreDataSource.password=geostore_pwd_da
geostoreEntityManagerFactory.jpaPropertyMap[hibernate.default_schema]=geostore

(in case you want to automatically create the geostore table set geostoreEntityManagerFactory.jpaPropertyMap[hibernate.hbm2ddl.auto]=update
then reset it to validate after the first execution)

6) run the staging_area geostore
7) run the dissemination_area geostore

8) copy the content of the directory WEB-INF/data to the GEOBATCH directory
9) set the ingestion.xml, publishing.xml and reprocess.xml according to the own setting parameters

10) run Geoserver 

11) deploy and enjoy
	
	
