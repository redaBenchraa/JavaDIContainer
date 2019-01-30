public class NewsService {
    String source;
    HttpService httpService;
    Communication communication;

    @Inject(defaultImplementation = SSH.class)
    Communication communicationField;

    @Inject(defaultImplementation = SSH.class)
    Communication communicationField2;

    @Inject
    public Logger MyLogger;

    @Inject(defaultImplementations = {TorHttpService.class})
    public NewsService(HttpService httpService, String source){
        this.httpService = httpService;
        this.source = source;
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    public String getSource() {
        return source;
    }

    public HttpService getHttpService() {
        return httpService;
    }

    public Communication getCommunication() {
        return communication;
    }
}
