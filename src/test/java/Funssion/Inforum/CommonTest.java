package Funssion.Inforum;

import Funssion.Inforum.common.utils.CustomStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
public class CommonTest {

    @Test
    public void test() {
        System.out.println(CustomStringUtils.getSearchStringList(" mar"));
    }
}
