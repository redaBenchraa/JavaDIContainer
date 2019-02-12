package TestClasses;
import BonjourDI.Inject;

public class HTTP extends Communication {
    public HttpService service0;
    HttpService service;
    @Inject
    public HttpService service2;

    @Inject(defaultImplementations = TorHttpService.class)
    public HttpService service3;

    public Logger logger;
    @Inject
    public Logger TimeLogger;
    Logger logger2;
    String url;
    @Inject(defaultImplementations = {TimeLogger.class})
    public  HTTP(Logger logger, HttpService h, String url){
        this.url = url;
        this.logger = logger;
        service0 = h;
    }

    public String getUrl() {
        return url;
    }

    public HttpService getService() {
        return service;
    }

    public void setService(HttpService service) {
        this.service = service;
    }

    public Logger getLogger2() {
        return logger2;
    }

    public void setLogger2(Logger logger2) {
        this.logger2 = logger2;
    }

}
