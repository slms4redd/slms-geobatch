    import java.io.File;


    
    public List execute(Object configuration, String eventFileName, Object listenerForwarder) throws Exception {
		
		Map props = configuration.getProperties();
	    String layer = props.get("layer");
	    String year = props.get("year");
	    String month = props.get("month");
	    String url = props.get("GeostoreURL");
	    String user = props.get("GeostoreUser");
	    String pwd = props.get("GeostorePwd");
		
		File file = new File(props.get("filename"));
        file.createNewFile();
		final List results = new ArrayList();
		return results;
    }