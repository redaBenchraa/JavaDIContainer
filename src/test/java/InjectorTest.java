import BonjourDI.BonjourDI;
import TestClasses.*;
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
    @DisplayName("US4 : Dependency Resolution")
    void testDependencyResolution() {
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
            assertTrue(((RssNewsService)newsService).rss instanceof RSS);
            assertTrue(newsService.MyLogger instanceof MyLogger);
            assertEquals(newsService.getCommunicationField(), newsService.getCommunicationField2());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("US0 : Test Objects and values")
    void testValueAndObjectInjection() {
        try {
            String value = "http://fakeurl";
            injector.bind(Communication.class).to(HTTP.class);
            injector.bind(HttpService.class).to(TorHttpService.class);
            injector.bind(Logger.class).to(MyLogger.class);
            injector.bind(String.class).to(value);
            HTTP newsService = (HTTP) injector.newInstance(Communication.class);
            assertEquals(value, newsService.getUrl());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("US1 : Setter injection")
    void testSetterInjection() {
        try {
            injector.bind(Communication.class).to(HTTP.class);
            injector.bind(HttpService.class).to(DarkWebHttpService.class);
            injector.bind(Logger.class).to(MyLogger.class);
            HTTP newsService = (HTTP) injector.newInstance(Communication.class);
            assertTrue(newsService.getService() instanceof DarkWebHttpService);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("US2 : Constructor injection")
    void testConstructorInjection() {
        try {
            injector.bind(Communication.class).to(HTTP.class);
            injector.bind(HttpService.class).to(DarkWebHttpService.class);
            injector.bind(Logger.class).to(MyLogger.class);
            HTTP newsService = (HTTP) injector.newInstance(Communication.class);
            assertTrue(newsService.service0 instanceof DarkWebHttpService);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("US3 : Field injection")
    void testFieldInjection() {
        try {
            injector.bind(Communication.class).to(HTTP.class);
            injector.bind(HttpService.class).to(DarkWebHttpService.class);
            injector.bind(Logger.class).to(MyLogger.class).asSingleton();

            HTTP newsService = (HTTP) injector.newInstance(Communication.class);
            assertTrue(newsService.service2 instanceof DarkWebHttpService);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("US5 : Support Singleton")
    void testSingleton() {
        try {
            injector.bind(Communication.class).to(HTTP.class);
            injector.bind(HttpService.class).to(DarkWebHttpService.class);
            injector.bind(Logger.class).to(TimeLogger.class).asSingleton();
            HTTP newsService = (HTTP) injector.newInstance(Communication.class);
            assertEquals(newsService.logger, newsService.getLogger2());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("US6 : Multiple Implementations")
    void testMultipleImplementationsInjection() {
        try {
            injector.bind(Communication.class).to(HTTP.class);
            injector.bind(HttpService.class).to(TorHttpService.class);
            injector.bind(HttpService.class).to(DarkWebHttpService.class);
            injector.bind(Logger.class).to(MyLogger.class);
            injector.bind(Logger.class).to(TimeLogger.class);
            HTTP newsService = (HTTP) injector.newInstance(Communication.class);
            assertTrue(newsService.logger instanceof TimeLogger);
            assertTrue(newsService.service3 instanceof TorHttpService);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("US8 : Autowiring")
    void testAutowiringInjection() {
        try {
            injector.bind(Communication.class).to(HTTP.class);
            injector.bind(HttpService.class).to(TorHttpService.class);
            injector.bind(Logger.class).to(TimeLogger.class);
            injector.bind(TimeLogger.class).forAutowiring();
            HTTP newsService = (HTTP) injector.newInstance(Communication.class);
            assertTrue(newsService.TimeLogger instanceof TimeLogger);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
}