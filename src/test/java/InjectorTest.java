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
        injector.bind(NewsService.class).to(RssNewsService.class);
        try {
            NewsService newsService = injector.newInstance(NewsService.class);
            assertTrue(newsService instanceof RssNewsService);
            assertTrue(newsService.getHttpService() instanceof DarkWebHttpService);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test value injection")
    void testValueInjection() {
        try {
            String value = "http://fakeurl";
            injector.bind(NewsService.class).to(RssNewsService.class);
            injector.bind(String.class).to(value);

            NewsService newsService = injector.newInstance(NewsService.class);

            assertEquals(value, newsService.getSource());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}