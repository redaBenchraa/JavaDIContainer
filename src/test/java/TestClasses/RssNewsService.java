package TestClasses;

import BonjourDI.Inject;

public class RssNewsService extends NewsService {
    public Communication rss;
    @Inject(defaultImplementations = {TorHttpService.class})
    public RssNewsService(HttpService httpService, String source) {
        super(httpService, source);
    }
    public Communication getRss() {
        return rss;
    }

    @Inject(defaultImplementation = RSS.class)
    public void setRss(Communication rss) {
        this.rss = rss;
    }

}
