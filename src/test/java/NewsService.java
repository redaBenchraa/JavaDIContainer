public class NewsService {
    HttpService httpService;
    public NewsService(HttpService httpService){
        this.httpService = httpService;
    }
    public HttpService getHttpService() {
        return httpService;
    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

}
