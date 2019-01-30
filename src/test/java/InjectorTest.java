import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InjectorTest {

    BonjourDI injector;

    @BeforeEach
    void createInjector() {
        injector = new BonjourDI();
    }

    @Test
    @DisplayName("Test simple injection")
    void testSimpleInjection() {
        injector.bind(HttpService.class).to(DarkWebHttpService.class);
        injector.bind(HttpService.class).to(TorHttpService.class);
        injector.bind(NewsService.class).to(RssNewsService.class);
        injector.bind(Communication.class).to(FTP.class).asSingleton();
        injector.bind(Communication.class).to(SSH.class).asSingleton();
        injector.bind(Communication.class).to(RSS.class).asSingleton();
        injector.bind(MyLogger.class).forAutowiring();
        try {
            NewsService newsService = injector.newInstance(NewsService.class);
            assertTrue(newsService instanceof RssNewsService);
            assertTrue(newsService.getHttpService() instanceof TorHttpService);
            assertTrue(newsService.getCommunication() instanceof FTP);
            assertTrue(newsService.communicationField instanceof SSH);
            assertTrue(((RssNewsService)newsService).rss instanceof RSS);
            assertTrue(newsService.MyLogger instanceof MyLogger);
            assertEquals(newsService.communicationField, newsService.communicationField2);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test value injection")
    void testValueInjection() {
        try {
            String value = "http://fakeurl";
            injector.bind(HttpService.class).to(DarkWebHttpService.class);
            injector.bind(HttpService.class).to(TorHttpService.class);
            injector.bind(NewsService.class).to(RssNewsService.class);
            injector.bind(Communication.class).to(FTP.class).asSingleton();
            injector.bind(MyLogger.class).forAutowiring();
            injector.bind(String.class).to(value);

            NewsService newsService = injector.newInstance(NewsService.class);

            assertEquals(value, newsService.getSource());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}