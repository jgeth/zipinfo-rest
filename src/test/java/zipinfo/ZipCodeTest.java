package zipinfo;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ZipCodeTest extends BaseTest {
    @Override
    @Before
    public void before(TestContext context) {
        super.before(context);
        //TODO add some test initialization code like security token retrieval
    }

    @Override
    @After
    public void after(TestContext context) {
        //TODO add some test end code like session destroy
        super.after(context);
    }

    @Test
    public void testGetZipCode(TestContext test) throws Exception {
        Async async = test.async(2);
        webClient.get("/badZip").send(ar -> {
            if (ar.succeeded()) {
                test.assertEquals(400, ar.result().statusCode());
                async.countDown();
            } else {
                test.fail(ar.cause());
            }
        });
        webClient.get("/97202").send(ar -> {
            if (ar.succeeded()) {
                // Do some asserts
                async.countDown();
            } else {
                test.fail(ar.cause());
            }
        });
    }
}
