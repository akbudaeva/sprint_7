package couriers;

import org.apache.commons.lang3.RandomStringUtils;

public class CourierData {

    public static Courier generateRandom() {
        final String login = RandomStringUtils.randomAlphabetic(10);
        final String password = RandomStringUtils.randomAlphabetic(10);
        final String firstName = RandomStringUtils.randomAlphabetic(10);
        return new Courier(login, password, firstName);
    }
}