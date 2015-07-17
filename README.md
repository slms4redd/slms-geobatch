

##Overview

###SLMS Overview
The objective of the UN-REDD SLMS - Satellite Landcover Monitoring System -  is to create an infrastructure that helps in the transparent dissemination of forest change, by processing and publishing forest (remote sensing) data, validated and open for public feedback of people in the monitored areas.

###SLMS Geobatch project Overviewre
This project is the core of batch data processing, based on the geobatch project https://github.com/geosolutions-it/geobatch.

nfms-geobatch project provide a custom implementation of data Ingestion, Processing, Statistic computation and dissemination.

##The SLMS Batch flows 
The Geobatch projects allow to develop processing **Actions** which can be chained into a **flow**.
Furthermore Geobatch offers out-of-the-box a set of geospatial-oriented actions that perform the most frequent processing tasks such as:

* Basic Raster processing (retiling, add overview)
* Basic Vector processing (datastore migrations, rasterization)
* Bulk publication of data on geoserver
* and much more...

The *standard geobatch actions* mixed with the *custom SLMS actions* contained in this repository create the batch flows used by the SLMS platform.

The flow configurations can be found in the (SLMS chef cookbook)[https://github.com/slms4redd/chef-slms-portal], see (here)[https://github.com/slms4redd/chef-slms-portal/tree/8227fd9b842b12c0afdb7a6636d21bf13fec5cfc/cookbooks/unredd-nfms-portal/files/default/geobatch_dir/config]

A quick summary of the currently supported flows:

### Timeseries data flows

- data related to several time instances - Statistic and charts of the changes through the years

* ingestionFlow Ingest timeseries datasets, both Raster (geoTIFF) and Vector (shp) and publish them on the **Staging** environment. This flow is also responsible for the image optimization (overview and internal retiling)

* reprocessFlow Compute statistics on timeseries data

* publishingFlow Publish timeseries layers and Statistics/Charts in the **dissemination** environment

### Static data flows

- data related to a single time instances - no stats computation

* ingestStaticRasterData Ingest Raster static data and publish them on the **Staging** environment as a single layer. This flow is also responsible for the image optimization (overview and internal retiling)

* ingestStaticVectorData Ingest Vector static data and publish them on the **Staging** environment as a single layer. It Take as input a shapefile (but also other format can easilly supported) and import it in the PostGIS database of the SLSM environment.

* publishStaticRasterData Publish a Raster static layer present in the staging environment in the **dissemination** environment.

* publishStaticVectorData Publish a Vector static layer present in the staging environment in the **dissemination** environment.

## Build and Run

From the root of the repo:

``
#$ cd src
#$ mvn clean install
``

the deploy the war in a tomcat6/java7 container.

The flows are developed to work in the SLMS environment so the war should deployed in the **stg_geobatch** tomcat instance of the SLMS environment.
A ready test environment can be setup running this (cookbook)[https://github.com/slms4redd/chef-slms-portal] on a fresh Ubuntu 12.04.5 Virtual Machine.

