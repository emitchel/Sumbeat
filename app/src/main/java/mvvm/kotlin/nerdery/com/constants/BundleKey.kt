package mvvm.kotlin.nerdery.com.constants

enum class BundleKey {
    ARTIST_NAME;

    override fun toString(): String {
        return name
    }
}