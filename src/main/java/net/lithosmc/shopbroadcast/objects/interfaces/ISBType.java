package net.lithosmc.shopbroadcast.objects.interfaces;

import net.kyori.adventure.text.format.Style;
import net.lithosmc.shopbroadcast.objects.ShopAd;

/**
 * An advertisement type is used
 * to specify how to style the advertisement
 * when it is displayed.
 *
 * Players can set which type their advertisement
 * is and have it styled accordingly.
 */
public interface ISBType {
    /**
     * The advertisement type that this interface represents.
     *
     * @return the name of the unique type in lowercase.
     */
    String getType();

    /**
     * Set type data on the shop ad if any persistent
     * data is needed to be associated with the type.
     *
     * @param shopAd Shop Advertisement being worked on.
     * @param dataArg Argument that the player passed in. Can be null.
     * @return the type data that should be associated,
     *         an empty string if no data is needed,
     *         or null if the data argument is invalid.
     */
    String setTypeData(ShopAd shopAd, String dataArg);

    /**
     * Indicate how the shop advertisement message should be styled.
     *
     * This allows for hover events and click events associated with
     * the message.
     *
     * @param shopAd Shop Advertisement to affect.
     * @return the style or null if no style.
     */
    Style styleBroadcast(ShopAd shopAd);
}
