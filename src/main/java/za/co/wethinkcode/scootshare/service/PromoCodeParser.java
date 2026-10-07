package za.co.wethinkcode.scootshare.service;

/**
 * Turns a promo code typed by a rider into a discount percentage.
 * Written years ago, no tests, and nobody remembers every rule.
 */
public class PromoCodeParser {

    public int discountPercent(String code) {
        if (code == null) {
            return 0;
        }
        String c = code.trim().toUpperCase();
        if (c.equals("FIRSTRIDE")) {
            return 25;
        }
        if (c.equals("STUDENT")) {
            return 15;
        }
        if (c.startsWith("SCOOT")) {
            try {
                int pct = Integer.parseInt(c.substring(5));
                if (pct >= 5 && pct <= 50 && pct % 5 == 0) {
                    return pct;
                }
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return 0;
    }
}
