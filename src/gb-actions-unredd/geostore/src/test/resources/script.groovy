    import it.geosolutions.geobatch.unredd.geostore.UNREDDGeostoreManager;
    
    public List execute(Object configuration, String eventFileName, Object listenerForwarder) throws Exception {
		System.out.println("sono nello script");
		Map props = configuration.getProperties();
	    String layer = props.get("layer");
	    String year = props.get("year");
	    String month = props.get("month");
	    String url = props.get("GeostoreURL");
	    String user = props.get("GeostoreUser");
	    String pwd = props.get("GeostorePwd");
		System.out.println("properties: ["+layer+","+year+","+month+"]");
		System.out.println("properties: ["+url+","+user+","+pwd+"]");
		UNREDDGeostoreManager manager = new UNREDDGeostoreManager(url,user, pwd);

		System.out.println("OOUTPUT"+manager.existLayer("layer1"));
	    // forwarding event to the next action
		// dummy results
		final List results = new ArrayList();
		return results;
    }