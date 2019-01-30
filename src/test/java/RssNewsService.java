public class RssNewsService extends NewsService {
    @Inject(defaultImplementations = {TorHttpService.class})
    public RssNewsService(HttpService httpService, String source) {
        super(httpService, source);
    }
}
