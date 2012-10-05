<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tsk="http://www.geo-solutions.it/tsk">
    <xsl:output method="text" omit-xml-declaration="yes" indent="no" />

    <xsl:template match="a">
        <xsl:value-of select="concat(' -a ', .)"/>
    </xsl:template>
    
    <xsl:template match="layer">
        <xsl:value-of select="concat(' -l ', .)"/>
    </xsl:template>
    
    <xsl:template match="src">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>
    
    <xsl:template match="outputfilename">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>
    
   
    <xsl:template match="size"> -ts <xsl:apply-templates select="width"/><xsl:apply-templates select="height"/></xsl:template>

    <xsl:template match="width">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>

    <xsl:template match="height">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>
    
    <xsl:template match="box"> -te <xsl:apply-templates select="rx0"/><xsl:apply-templates select="ry0"/><xsl:apply-templates select="rx1"/><xsl:apply-templates select="ry1"/></xsl:template>
    
    <xsl:template match="rx0">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>

    <xsl:template match="ry0">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>
    
    <xsl:template match="rx1">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>

    <xsl:template match="ry1">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>
    
    <xsl:template match="nodata">
        <xsl:value-of select="concat(' -a_nodata ', .)"/>
    </xsl:template>
    
    <xsl:template match="co">
        <xsl:value-of select="concat(' -co &quot;', .,'&quot;')"/>
    </xsl:template>
    
    <xsl:template match="of">
        <xsl:value-of select="concat(' -of ', .)"/>
    </xsl:template>

    <xsl:template match="a_srs">
        <xsl:value-of select="concat(' -a_srs ', .)"/>
    </xsl:template>
    
    <xsl:template match="ot">
        <xsl:value-of select="concat(' -ot ', .)"/>
    </xsl:template>
    
    <xsl:template match="where">
        <xsl:value-of select="concat(' -where &quot;',.,'&quot;')"/>
    </xsl:template>
    
</xsl:stylesheet>