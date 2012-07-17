
-- THIS IS THE PART FOR CREATING THE STAGING AREA USERS

--DATABASE PART

-- CREATE DATABASE staging_area TEMPLATE = template_postgis;


-- CREATE AND GRANTS USER FOR GEOSTORE STAGING
CREATE user geostore_user_da LOGIN PASSWORD 'geostore_pwd_da' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

CREATE SCHEMA geostore;

GRANT USAGE ON SCHEMA geostore TO geostore_user_da;
GRANT ALL ON SCHEMA geostore TO geostore_user_da;

GRANT SELECT ON public.spatial_ref_sys to geostore_user_da;
GRANT SELECT,INSERT,DELETE ON public.geometry_columns to geostore_user_da;


--THIS PART CREATE THE USER FOR MANAGING THE FEATURES INTO THE STAGING AREA


--USER PART

CREATE user features_user_da LOGIN PASSWORD 'features_pwd_da' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

CREATE SCHEMA features;

GRANT USAGE ON SCHEMA features TO features_user_da;
GRANT ALL ON SCHEMA features TO features_user_da;

GRANT SELECT ON public.spatial_ref_sys to features_user_da;
GRANT SELECT,INSERT,DELETE ON public.geometry_columns to features_user_da;


--THIS PART CREATE THE  USER FOR MANAGING THE MOSAIC INTO THE STAGING AREA


--USER PART

CREATE user mosaic_user_da LOGIN PASSWORD 'mosaic_pwd_da' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

CREATE SCHEMA mosaic;

GRANT USAGE ON SCHEMA mosaic TO mosaic_user_da;
GRANT ALL ON SCHEMA mosaic TO mosaic_user_da;

GRANT SELECT ON public.spatial_ref_sys to mosaic_user_da;
GRANT SELECT,INSERT,DELETE ON public.geometry_columns to mosaic_user_da;


