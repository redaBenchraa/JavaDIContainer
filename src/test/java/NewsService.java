public class NewsService {
    String source;
    HttpService httpService;
    public NewsService(HttpService httpService, String source){
        this.httpService = httpService;
        this.source = source;
    }
    public HttpService getHttpService() {
        return httpService;
    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


}
