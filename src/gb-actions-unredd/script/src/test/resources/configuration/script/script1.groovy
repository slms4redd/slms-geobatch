    import java.io.File;


    
    public Map execute(Map argsMap) throws Exception {
        System.out.println("Running script Script1.groovy");

		
		File file = new File("target/script1output.xml");
        file.createNewFile();
        final Map results = new HashMap();
		return results;
    }