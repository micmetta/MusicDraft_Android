package com.example.musicdraft.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicdraft.data.tables.artisti.Artisti
import com.example.musicdraft.data.tables.artisti.ArtistiDao
import com.example.musicdraft.data.tables.deck.DaoDeck
import com.example.musicdraft.data.tables.deck.Deck
import com.example.musicdraft.data.tables.exchange_management_cards.ConvertersExchangeManagementCards
import com.example.musicdraft.data.tables.exchange_management_cards.ExchangeManagementCards
import com.example.musicdraft.data.tables.exchange_management_cards.ExchangeManagementCardsDao
import com.example.musicdraft.data.tables.handleFriends.HandleFriends
import com.example.musicdraft.data.tables.handleFriends.HandleFriendsDao
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcluded
import com.example.musicdraft.data.tables.matchmaking.MatchSummaryConcludedDao
import com.example.musicdraft.data.tables.matchmaking.Matchmaking
import com.example.musicdraft.data.tables.matchmaking.MatchmakingDao
import com.example.musicdraft.data.tables.track.Track
import com.example.musicdraft.data.tables.track.TrackDao
import com.example.musicdraft.data.tables.user.User
import com.example.musicdraft.data.tables.user.UserDao
import com.example.musicdraft.data.tables.user_cards.UCADao
import com.example.musicdraft.data.tables.user_cards.UCTDao
import com.example.musicdraft.data.tables.user_cards.User_Cards_Artisti
import com.example.musicdraft.data.tables.user_cards.User_Cards_Track
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Artisti::class, Track::class, User_Cards_Artisti::class, User_Cards_Track::class, HandleFriends::class, ExchangeManagementCards:: class, Deck::class, Matchmaking:: class, MatchSummaryConcluded:: class],
    version = 1
)
@TypeConverters(ConvertersExchangeManagementCards::class) // per poter usare i convertitori per la tabella 'ExchangeManagementCards' c'era prima..
//@TypeConverters(
    //ConvertersExchangeManagementCards::class, // per poter usare i convertitori per la tabella 'ExchangeManagementCards'
    //ConvertersMatchSummaryConcluded::class // per poter usare i convertitori per la tabella 'MatchSummaryConcluded'
//)
abstract class MusicDraftDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao?
    abstract fun artistDao(): ArtistiDao?
    abstract fun trackDao():TrackDao?
    abstract fun ownArtCardsDao():UCADao?
    abstract fun ownTrackCardsDao():UCTDao?
    abstract fun handleFriendsDao(): HandleFriendsDao?
    abstract fun ExchangeManagementCardsDao(): ExchangeManagementCardsDao?
    abstract fun deckDao(): DaoDeck?
    abstract fun matchmakingDao(): MatchmakingDao?
    abstract fun matchSummaryConcludedDao(): MatchSummaryConcludedDao?

    companion object {
        // marking the instance as volatile to ensure atomic access to the variable
        @Volatile
        private var INSTANCE: MusicDraftDatabase? = null


        fun getDatabase(context: Context): MusicDraftDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDraftDatabase::class.java,
                    "musicDraftDB"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateArtisti(database.artistDao()!!)
                                populateTracks(database.trackDao()!!)
                            }
                        }
                    }

                    private suspend fun populateTracks(dao: TrackDao) {
                        val file = "[{\"id\":\"00xLoYaAPqR76gf0t06EWX\", \"anno_pubblicazione\":\"2016-01-15\", \"durata\":\"2minuti e 40secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733a6d59ebf7adcf185541bf44\", \"nome\":\"Abby's Song\", \"popolarita\":14},\n" +
                                " {\"id\":\"01ZEhzcrkUgtTBRjY3GKfC\", \"anno_pubblicazione\":\"2017-03-24\", \"durata\":\"3minuti e 1secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273fdcce838af923549cbc880f6\", \"nome\":\"I'm Good\", \"popolarita\":36},\n" +
                                " {\"id\":\"03I0kk32IsoywW4tLeLALr\", \"anno_pubblicazione\":\"2024-05-31\", \"durata\":\"1minuti e 44secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273f430933c2f8d848e51402071\", \"nome\":\"numb\", \"popolarita\":26},\n" +
                                " {\"id\":\"0432y1pTzvglrnbyZC5GNj\", \"anno_pubblicazione\":\"2023-07-18\", \"durata\":\"1minuti e 57secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273ae0a1a601449d7a0d3fdb25f\", \"nome\":\"abysmal stars\", \"popolarita\":17},\n" +
                                " {\"id\":\"05UKPuq9D7C6YDaps38ReU\", \"anno_pubblicazione\":\"2022-09-30\", \"durata\":\"2minuti e 57secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2730dfbbc7bdf9ac078758cfdcb\", \"nome\":\"numb (feat. blackbear)\", \"popolarita\":39},\n" +
                                " {\"id\":\"06Ud0p4auVvRLrew489vO8\", \"anno_pubblicazione\":\"2024-02-26\", \"durata\":\"1minuti e 38secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27333a051275a250c4f86adc353\", \"nome\":\"numb\", \"popolarita\":34},\n" +
                                " {\"id\":\"078SaHjGDGJUMveb6bPnHf\", \"anno_pubblicazione\":\"2024-03-08\", \"durata\":\"2minuti e 35secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273342f93041adf3a882dc3f046\", \"nome\":\"numb\", \"popolarita\":28},\n" +
                                " {\"id\":\"08PgEBgGK65DZpxudS1J73\", \"anno_pubblicazione\":\"2023-11-03\", \"durata\":\"2minuti e 59secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27331866564a5b903800500f4ab\", \"nome\":\"my little tony\", \"popolarita\":44},\n" +
                                " {\"id\":\"0CRQWPjy7QKTrKYA78tHSs\", \"anno_pubblicazione\":\"2017-05-17\", \"durata\":\"2minuti e 50secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27339c9b7b1779bd8e21b754ebf\", \"nome\":\"No Bar Code\", \"popolarita\":9},\n" +
                                " {\"id\":\"0dOQY6y45kmhus8o694yz2\", \"anno_pubblicazione\":\"2020-01-10\", \"durata\":\"2minuti e 35secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273b8144ab9f076bfff3c704e6c\", \"nome\":\"Acualta\", \"popolarita\":25},\n" +
                                " {\"id\":\"0eDUXUx4BVHETGkyPm1EWA\", \"anno_pubblicazione\":\"2023-11-15\", \"durata\":\"3minuti e 7secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273f1e14d6b50725912cb75f49e\", \"nome\":\"That's All I Want To Do\", \"popolarita\":18},\n" +
                                " {\"id\":\"0faS2ALMgteu6UQpdv9EBb\", \"anno_pubblicazione\":\"2021-09-10\", \"durata\":\"2minuti e 30secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2732f82be3d07e704436340243e\", \"nome\":\"Bar Last Night\", \"popolarita\":23},\n" +
                                " {\"id\":\"0fKM9f3nfuM6WbU81TpB1v\", \"anno_pubblicazione\":\"2024-04-05\", \"durata\":\"1minuti e 38secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2736d3d6995a65bb640c7884bb2\", \"nome\":\"NUMB\", \"popolarita\":18},\n" +
                                " {\"id\":\"0Fvi0AGyCCrI8jOXc75fPt\", \"anno_pubblicazione\":\"2023-08-28\", \"durata\":\"3minuti e 0secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2736065ca4b729bd1b4be469d27\", \"nome\":\"ON MY LIFE\", \"popolarita\":30},\n" +
                                " {\"id\":\"0fxf9dkUc8r8AHWPx7Cdkn\", \"anno_pubblicazione\":\"2024-03-13\", \"durata\":\"2minuti e 3secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27346b395c80f719c266ce58d34\", \"nome\":\"With You - Demo\", \"popolarita\":27},\n" +
                                " {\"id\":\"0gee5QGKMarvk5iluMe07W\", \"anno_pubblicazione\":\"2019-09-09\", \"durata\":\"3minuti e 40secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733ea966931475896f491c752b\", \"nome\":\"Bar Exam 3\", \"popolarita\":11},\n" +
                                " {\"id\":\"0GfsQlt4Xn3WERNNydq0iu\", \"anno_pubblicazione\":\"2023-11-24\", \"durata\":\"1minuti e 12secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733b4eee56c8c2fd8a7e765d9f\", \"nome\":\"Let Me Hit It (ABE201)\", \"popolarita\":18},\n" +
                                " {\"id\":\"0GvVWR6HijJkACXOfQE3wK\", \"anno_pubblicazione\":\"2023-07-07\", \"durata\":\"1minuti e 45secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273880851757cc84d6a57b2f624\", \"nome\":\"numb\", \"popolarita\":53},\n" +
                                " {\"id\":\"0HL5B51NcFanlPgcj6dq9w\", \"anno_pubblicazione\":\"2021-02-19\", \"durata\":\"2minuti e 38secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2738e76f6ecf4fb6fc3b2b5b879\", \"nome\":\"bar101 (feat. Chris Emond)\", \"popolarita\":8},\n" +
                                " {\"id\":\"0iiSZYwelok9ZJRU0b7Fjc\", \"anno_pubblicazione\":\"2024-01-13\", \"durata\":\"3minuti e 31secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733f413440a4482153446f2581\", \"nome\":\"Watching\", \"popolarita\":14},\n" +
                                " {\"id\":\"0JtxNPF9f1cFi4HsxtcYwA\", \"anno_pubblicazione\":\"2022-09-14\", \"durata\":\"2minuti e 8secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273cf3339ca9efa01675828f39b\", \"nome\":\"abzu\", \"popolarita\":16},\n" +
                                " {\"id\":\"0kMWWZzHQwB5HaIkWhObP5\", \"anno_pubblicazione\":\"2024-05-03\", \"durata\":\"3minuti e 12secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273469007d899cbb4ad99478cde\", \"nome\":\"Numb\", \"popolarita\":33},\n" +
                                " {\"id\":\"0L7IFmbFW9MFUkwF0cdJZc\", \"anno_pubblicazione\":\"2022-07-08\", \"durata\":\"6minuti e 3secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273ef53f94bafd42159bc0bbc5f\", \"nome\":\"numb\", \"popolarita\":35},\n" +
                                " {\"id\":\"0qsZ4JWNIxqMpCNHOfaop9\", \"anno_pubblicazione\":\"2022-01-06\", \"durata\":\"3minuti e 22secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273632515b9953a059e94eb7d60\", \"nome\":\"Ni Por Cualto Ni Por Fama\", \"popolarita\":25},\n" +
                                " {\"id\":\"0sLXELE8DseFeY8cJgMV5W\", \"anno_pubblicazione\":\"2023-08-03\", \"durata\":\"2minuti e 22secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c6e30e260b5940febfbe7cae\", \"nome\":\"ABN\", \"popolarita\":28},\n" +
                                " {\"id\":\"15mEZWV8zsXtlQuq3fnv5C\", \"anno_pubblicazione\":\"2022-10-21\", \"durata\":\"2minuti e 58secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735261e3b2c22426865a490f9e\", \"nome\":\"Pool Party\", \"popolarita\":17},\n" +
                                " {\"id\":\"184VpznTM0M4NC1j6TIqVr\", \"anno_pubblicazione\":\"2010-02-02\", \"durata\":\"4minuti e 8secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27316faac64939b323e7907b3a8\", \"nome\":\"Numb\", \"popolarita\":17},\n" +
                                " {\"id\":\"18mV837bYoLSA0daClqPc3\", \"anno_pubblicazione\":\"2020-01-06\", \"durata\":\"2minuti e 4secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27325177bf7449cf76e93020778\", \"nome\":\"Numb\", \"popolarita\":36},\n" +
                                " {\"id\":\"199XWHDsI50w7eteTMqAGv\", \"anno_pubblicazione\":\"2023-06-21\", \"durata\":\"2minuti e 5secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273fce54e622230b32a414f78d6\", \"nome\":\"Numb\", \"popolarita\":25},\n" +
                                " {\"id\":\"19MaRXS4tmaJTMUOjorwQC\", \"anno_pubblicazione\":\"2024-02-09\", \"durata\":\"2minuti e 58secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273efdeed0cdb48c68a2486b9f4\", \"nome\":\"Sorry\", \"popolarita\":18},\n" +
                                " {\"id\":\"1CuuxMLq0qQ2nxqZu5a0k4\", \"anno_pubblicazione\":\"2021-06-16\", \"durata\":\"3minuti e 1secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735a3e046cb80125c3b107b7ed\", \"nome\":\"Barbed Wire Watch\", \"popolarita\":26},\n" +
                                " {\"id\":\"1dcPaYn2FsLHgMfmbTneHt\", \"anno_pubblicazione\":\"2017-08-18\", \"durata\":\"4minuti e 41secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27379c44367d0a7c83b4dc3e1b2\", \"nome\":\"Numb\", \"popolarita\":29},\n" +
                                " {\"id\":\"1DTg4oUkBKNK62MxyEBruu\", \"anno_pubblicazione\":\"2022-05-02\", \"durata\":\"2minuti e 5secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273aa802e8561ed20eca8328f85\", \"nome\":\"Bar\", \"popolarita\":25},\n" +
                                " {\"id\":\"1DZFuFNH8vSTK15JokkdhX\", \"anno_pubblicazione\":\"2024-05-17\", \"durata\":\"2minuti e 30secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2732fd631fdc4d53d665487c8c4\", \"nome\":\"Lanova's Freestyle\", \"popolarita\":32},\n" +
                                " {\"id\":\"1fw77QIyR8rXYl2KVN2o7T\", \"anno_pubblicazione\":\"2024-04-19\", \"durata\":\"2minuti e 7secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27311f82a13212252903e6ef9b0\", \"nome\":\"Dive Bar\", \"popolarita\":28},\n" +
                                " {\"id\":\"1HlAOfPFRqeyAfCvMfhjxd\", \"anno_pubblicazione\":\"2023-01-20\", \"durata\":\"3minuti e 4secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2738e74cfb801e7f3a413dcfcb0\", \"nome\":\"Abra Kadabra\", \"popolarita\":20},\n" +
                                " {\"id\":\"1JYYjFknVBubfrDYX6iKUo\", \"anno_pubblicazione\":\"2020-10-27\", \"durata\":\"0minuti e 43secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273bf076bd9b8ad8facbb19f473\", \"nome\":\"Intro (Side A)\", \"popolarita\":21},\n" +
                                " {\"id\":\"1NBRpCH5bimvOtHJWiESAz\", \"anno_pubblicazione\":\"2023-10-06\", \"durata\":\"3minuti e 13secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27340fd73e7af2e16c2ee508367\", \"nome\":\"Milk\", \"popolarita\":27},\n" +
                                " {\"id\":\"1O9iXsMTuuFTLvepCoTB2M\", \"anno_pubblicazione\":\"2017-08-25\", \"durata\":\"1minuti e 50secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2730aaca95a040bd8ddcde7ed74\", \"nome\":\"Ab\", \"popolarita\":22},\n" +
                                " {\"id\":\"1OtLASdp4IM778ISRmdSUD\", \"anno_pubblicazione\":\"2022-09-10\", \"durata\":\"2minuti e 17secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e0f19bcfb6d646df4f6a9b39\", \"nome\":\"Chill Bar Background Music\", \"popolarita\":26},\n" +
                                " {\"id\":\"1PcJJe4WWo8K2MGjYEPIMj\", \"anno_pubblicazione\":\"2024-02-16\", \"durata\":\"1minuti e 16secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27362458ac50ddf02863cb27eed\", \"nome\":\"aby\$\$\", \"popolarita\":36},\n" +
                                " {\"id\":\"1pPj8RMhHZLWz4KtIM36K8\", \"anno_pubblicazione\":\"2024-05-02\", \"durata\":\"2minuti e 17secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273481a7a55519429f48f136133\", \"nome\":\"You\\u2019ll Be Fine\", \"popolarita\":19},\n" +
                                " {\"id\":\"1q90SIMBYQcabxqg4CBUZ0\", \"anno_pubblicazione\":\"2016-11-04\", \"durata\":\"4minuti e 10secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273ba4f8a7a7a68627f4b8d33d4\", \"nome\":\"A.B.N.\", \"popolarita\":16},\n" +
                                " {\"id\":\"1qji8Z8rXS3KvFofyRRCjE\", \"anno_pubblicazione\":\"2023-10-06\", \"durata\":\"1minuti e 53secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273213ceaeffcb78d83f7d0a7e0\", \"nome\":\"numb\", \"popolarita\":25},\n" +
                                " {\"id\":\"1tFNrfMT4dK2bvyBoDsALx\", \"anno_pubblicazione\":\"2008\", \"durata\":\"4minuti e 32secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e2e2295b2b47f6bbcbbe7265\", \"nome\":\"Sos\", \"popolarita\":11},\n" +
                                " {\"id\":\"1TvKCVRVcX7T9AXe99Rqsx\", \"anno_pubblicazione\":\"2024-04-22\", \"durata\":\"2minuti e 25secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2738af9bca8338c645c3a4f05fe\", \"nome\":\"Abnormal\", \"popolarita\":17},\n" +
                                " {\"id\":\"1uH9gB4V12Ua9cGWfghC6c\", \"anno_pubblicazione\":\"2023-08-25\", \"durata\":\"0minuti e 55secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273ea2427ee6e0664c7e094c530\", \"nome\":\"Vessel\", \"popolarita\":15},\n" +
                                " {\"id\":\"1Usj6PiGnp1LgNteMTS8aK\", \"anno_pubblicazione\":\"2023-06-09\", \"durata\":\"2minuti e 1secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2732f941601b41e684755bd8cb0\", \"nome\":\"numb (Slowed + Reverb)\", \"popolarita\":53},\n" +
                                " {\"id\":\"1vlZcAI0uqRJSuzFLvhnOl\", \"anno_pubblicazione\":\"2021-06-13\", \"durata\":\"2minuti e 40secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a8475a9bbddd8beb86c515ab\", \"nome\":\"Bar For Bar\", \"popolarita\":19},\n" +
                                " {\"id\":\"1w5mmyEciPwA8xWRf5bnOF\", \"anno_pubblicazione\":\"2023-07-21\", \"durata\":\"2minuti e 3secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2736de7697935cd4dd6ab8d7dcc\", \"nome\":\"Numb\", \"popolarita\":24},\n" +
                                " {\"id\":\"1WSAgK0FB0SOt0TPQ3uS6I\", \"anno_pubblicazione\":\"2014-01-21\", \"durata\":\"3minuti e 57secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e6276cf8dc7857709d43e3ad\", \"nome\":\"Bar Mirror\", \"popolarita\":25},\n" +
                                " {\"id\":\"1Xa39Oimf0BbgxfoYqa2Ez\", \"anno_pubblicazione\":\"2023-10-19\", \"durata\":\"3minuti e 10secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2734bc62fb54ffb8175acb27d93\", \"nome\":\"Bar Is On The Floor\", \"popolarita\":37},\n" +
                                " {\"id\":\"21FzL6mx0YHlZXsnaZBkk8\", \"anno_pubblicazione\":\"2014-06-24\", \"durata\":\"3minuti e 25secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c182282569d8fa0f8b0acae4\", \"nome\":\"Closure\", \"popolarita\":21},\n" +
                                " {\"id\":\"2412v74DRayyZ0cAdCRpe0\", \"anno_pubblicazione\":\"2022-07-29\", \"durata\":\"4minuti e 21secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273534e615b5bcee045b688b18a\", \"nome\":\"Home\", \"popolarita\":15},\n" +
                                " {\"id\":\"25hX7Pwk5SvAyoXa95rAcJ\", \"anno_pubblicazione\":\"2024-01-05\", \"durata\":\"2minuti e 42secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a138c22bd2122719d067dfae\", \"nome\":\"IWU\", \"popolarita\":31},\n" +
                                " {\"id\":\"271Rc79id4cLeNssibhGn2\", \"anno_pubblicazione\":\"2023-12-25\", \"durata\":\"2minuti e 20secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733795d3f33c8f242cb1eaf494\", \"nome\":\"Journey\", \"popolarita\":22},\n" +
                                " {\"id\":\"2AcRrlMtUaWuxkOKFX7ZG5\", \"anno_pubblicazione\":\"2022-05-06\", \"durata\":\"2minuti e 46secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733b0947eb24eca1a9186dfd4b\", \"nome\":\"NOISE\", \"popolarita\":12},\n" +
                                " {\"id\":\"2bTtUoo79KLturZ9d86aJI\", \"anno_pubblicazione\":\"2022-09-08\", \"durata\":\"2minuti e 31secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273eef01281757119e2b6793f2d\", \"nome\":\"Numb The Pain\", \"popolarita\":18},\n" +
                                " {\"id\":\"2c38115BTAeYvOHv5l7ZNV\", \"anno_pubblicazione\":\"2021-10-28\", \"durata\":\"3minuti e 7secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273aa56f7dd1d20183bb9074366\", \"nome\":\"Bar for Bar, Pt.2\", \"popolarita\":23},\n" +
                                " {\"id\":\"2E27sqZQmFJiCbQNx8SX9v\", \"anno_pubblicazione\":\"2024-02-29\", \"durata\":\"2minuti e 12secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2730791c52a43bd6e91563646ce\", \"nome\":\"Restless\", \"popolarita\":27},\n" +
                                " {\"id\":\"2fOpK9RvF6VIv4eSzpjyzB\", \"anno_pubblicazione\":\"2023-03-03\", \"durata\":\"2minuti e 7secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273f72be265728a54b026423e43\", \"nome\":\"numb myself~\", \"popolarita\":38},\n" +
                                " {\"id\":\"2g6BHRenF1wy5YGo8CR313\", \"anno_pubblicazione\":\"2023-11-02\", \"durata\":\"1minuti e 42secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2737a68122334e0811367501d8d\", \"nome\":\"Letting Go 2\", \"popolarita\":18},\n" +
                                " {\"id\":\"2J5LaGGW5hsKHHaNL3ITH7\", \"anno_pubblicazione\":\"2022-04-13\", \"durata\":\"2minuti e 26secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273d58e1fa8fd2a5f1dcf7bcf41\", \"nome\":\"Numb\", \"popolarita\":27},\n" +
                                " {\"id\":\"2loNojs7JFvqpJBF9HkeeN\", \"anno_pubblicazione\":\"2019-05-10\", \"durata\":\"2minuti e 46secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273fe42010fca3760e4543586e5\", \"nome\":\"RUN\", \"popolarita\":24},\n" +
                                " {\"id\":\"2Na5wRs4gRGI4TDpxytQ7r\", \"anno_pubblicazione\":\"2017-01-04\", \"durata\":\"1minuti e 17secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27368de73459d68484fe4c2a7dc\", \"nome\":\"Numb\", \"popolarita\":29},\n" +
                                " {\"id\":\"2P1NhphzL2ae55OH9aZcvM\", \"anno_pubblicazione\":\"2023-11-03\", \"durata\":\"2minuti e 20secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273fc0ce91025cd0edfebc4363f\", \"nome\":\"Abnormal\", \"popolarita\":16},\n" +
                                " {\"id\":\"2qvdVgpyw7kkXYv1hpZCYG\", \"anno_pubblicazione\":\"2021-03-26\", \"durata\":\"3minuti e 23secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c31455f5316cb8d8daaf8226\", \"nome\":\"Bars\", \"popolarita\":18},\n" +
                                " {\"id\":\"2TDuIj2l8xSyOSZdN7Q6PM\", \"anno_pubblicazione\":\"2022-11-09\", \"durata\":\"2minuti e 2secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273f640fc2ac07fe4dfc02c7e7e\", \"nome\":\"Abubu Francesita (Phonk)\", \"popolarita\":27},\n" +
                                " {\"id\":\"2vdR8jHeIVKLlvQOGEHdNl\", \"anno_pubblicazione\":\"2023-10-27\", \"durata\":\"2minuti e 41secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735d029c88737bc7b38cc350d8\", \"nome\":\"Bar Fights & Poetry\", \"popolarita\":42},\n" +
                                " {\"id\":\"2W2BNqz413sw0UGdKEjVpi\", \"anno_pubblicazione\":\"2023-01-28\", \"durata\":\"2minuti e 44secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733057395de711ac9a743581de\", \"nome\":\"Barbaric Barz\", \"popolarita\":19},\n" +
                                " {\"id\":\"2ZoNUQvyvm00Kgkox7da3x\", \"anno_pubblicazione\":\"2014-10-14\", \"durata\":\"2minuti e 50secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27385d511d392cd4eb8add13353\", \"nome\":\"Numb\", \"popolarita\":29},\n" +
                                " {\"id\":\"32LLYeHN4VC1VX8rqF7PdS\", \"anno_pubblicazione\":\"2024-02-16\", \"durata\":\"2minuti e 21secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27362458ac50ddf02863cb27eed\", \"nome\":\"numb nectar\", \"popolarita\":34},\n" +
                                " {\"id\":\"38R3V0KPmttuzLf5vNjhhY\", \"anno_pubblicazione\":\"2024-02-22\", \"durata\":\"2minuti e 45secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273815a8f1a4652f96e7a5aad1e\", \"nome\":\"LOVE\", \"popolarita\":12},\n" +
                                " {\"id\":\"3bfXWnd8wch8vmBhPsLkfW\", \"anno_pubblicazione\":\"2024-04-09\", \"durata\":\"3minuti e 1secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2739d7f5ac092d328ca3b65de0b\", \"nome\":\"Numb\", \"popolarita\":26},\n" +
                                " {\"id\":\"3erlbv6s5bAzAQKDFCDqqj\", \"anno_pubblicazione\":\"2023-04-14\", \"durata\":\"4minuti e 11secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27365ce3cd27edeb1a1420b14fa\", \"nome\":\"Numb\", \"popolarita\":28},\n" +
                                " {\"id\":\"3EtUDgYSusB094HSEOFLhz\", \"anno_pubblicazione\":\"1985-02-25\", \"durata\":\"4minuti e 50secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273802b1875a94839f2bf79ca30\", \"nome\":\"BY THE END OF THE CENTURY\", \"popolarita\":18},\n" +
                                " {\"id\":\"3gfULpXswOBwrEcJB7Q9On\", \"anno_pubblicazione\":\"2022-07-15\", \"durata\":\"3minuti e 4secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c1525d75c3db0cd719cdf063\", \"nome\":\"Bar Mediterraneo\", \"popolarita\":38},\n" +
                                " {\"id\":\"3J9fEHD1NDDJJ9rYqJNMxL\", \"anno_pubblicazione\":\"2020-09-04\", \"durata\":\"1minuti e 30secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27350277418d1e4848f976bac82\", \"nome\":\"inbeth ubdream\", \"popolarita\":28},\n" +
                                " {\"id\":\"3JRZiuZebLbHuPTEwCORuX\", \"anno_pubblicazione\":\"2022-07-21\", \"durata\":\"1minuti e 35secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273ccdba6c05d5efa9aec74323a\", \"nome\":\"Bar\", \"popolarita\":20},\n" +
                                " {\"id\":\"3jXH5mNvp5zqDMi0DeCQkQ\", \"anno_pubblicazione\":\"2024-01-18\", \"durata\":\"2minuti e 11secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273598c958c37e0f1949cbd2e2f\", \"nome\":\"Abnormal Brain\", \"popolarita\":29},\n" +
                                " {\"id\":\"3L3FnIbZBzgRRpc0Qr0tdl\", \"anno_pubblicazione\":\"2023-12-25\", \"durata\":\"3minuti e 9secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733795d3f33c8f242cb1eaf494\", \"nome\":\"Don't Let Go\", \"popolarita\":22},\n" +
                                " {\"id\":\"3MQU5aMaGQHyytRh71eDwz\", \"anno_pubblicazione\":\"2023-04-04\", \"durata\":\"2minuti e 22secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2739e6fc0289b35871856081e8b\", \"nome\":\"lealtad\", \"popolarita\":25},\n" +
                                " {\"id\":\"3OJ2YZNGYejZ1BSr3VCdVf\", \"anno_pubblicazione\":\"2012-08-30\", \"durata\":\"4minuti e 37secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733aa3623aabf1550f71a8cc9b\", \"nome\":\"Maria - Club Mix\", \"popolarita\":7},\n" +
                                " {\"id\":\"3Ou8zmgF7IbWm13BeXLdsc\", \"anno_pubblicazione\":\"2023-02-13\", \"durata\":\"2minuti e 15secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27300c9c1187569b6541afa6e98\", \"nome\":\"Numb - Sped Up\", \"popolarita\":36},\n" +
                                " {\"id\":\"3pDHvtj2zfFxwBZO62cZLx\", \"anno_pubblicazione\":\"2019-03-20\", \"durata\":\"2minuti e 39secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733c760e34e93fb0015a910516\", \"nome\":\"Numb - Remix\", \"popolarita\":25},\n" +
                                " {\"id\":\"3Q18pOa9VXToVslHADHg0N\", \"anno_pubblicazione\":\"2024-04-19\", \"durata\":\"1minuti e 52secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273d5061f1833e96d8abb851537\", \"nome\":\"Numb\", \"popolarita\":17},\n" +
                                " {\"id\":\"3tJnZQLZiZmeQ8eRwy21QE\", \"anno_pubblicazione\":\"2019-12-29\", \"durata\":\"1minuti e 51secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27390c06739896f0b6b3ab26a54\", \"nome\":\"Whole Lotta Money\", \"popolarita\":16},\n" +
                                " {\"id\":\"3uPD64gVqUjZycPLOspeDj\", \"anno_pubblicazione\":\"2020-07-30\", \"durata\":\"2minuti e 24secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273661dbc5eb68cbf000e2fb89d\", \"nome\":\"Bars of Death\", \"popolarita\":22},\n" +
                                " {\"id\":\"3Y2khY1lCNfbygFPUqtvW1\", \"anno_pubblicazione\":\"2023-05-01\", \"durata\":\"1minuti e 10secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e247f984743d1d1fcd32293a\", \"nome\":\"A B\", \"popolarita\":27},\n" +
                                " {\"id\":\"42cBTPrRP2jfX317lEo3Ck\", \"anno_pubblicazione\":\"2022-01-02\", \"durata\":\"1minuti e 53secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735c9379c46dc4582458580fb0\", \"nome\":\"Pit Not The Palace\", \"popolarita\":27},\n" +
                                " {\"id\":\"44aWpggvap2LHgmJ8B8CFr\", \"anno_pubblicazione\":\"2022-10-10\", \"durata\":\"2minuti e 18secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c86ea2dcc0150e459d97d6a8\", \"nome\":\"Bar\", \"popolarita\":10},\n" +
                                " {\"id\":\"44QCS8DWUQT7HfaiZbxFLK\", \"anno_pubblicazione\":\"2019-08-16\", \"durata\":\"3minuti e 47secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2737342f119b4e64353971950c0\", \"nome\":\"Issues\", \"popolarita\":5},\n" +
                                " {\"id\":\"49gmMR6bpM4AIWm9RP1Cm4\", \"anno_pubblicazione\":\"2024-04-18\", \"durata\":\"2minuti e 48secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a3de96c5d94052660d033c1c\", \"nome\":\"A Bar Song (Tipsy) (Originally Performed by Shaboozey) [Instrumental]\", \"popolarita\":22},\n" +
                                " {\"id\":\"4aPVQY7naxAPlR1X16Neut\", \"anno_pubblicazione\":\"2024-01-01\", \"durata\":\"3minuti e 7secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27347fff6ef5d9e1dbce4a87756\", \"nome\":\"Abby\", \"popolarita\":15},\n" +
                                " {\"id\":\"4cEMDpdqW7y3MAFpkmBj9w\", \"anno_pubblicazione\":\"2023-11-30\", \"durata\":\"3minuti e 2secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27316d2657c91c430e898284854\", \"nome\":\"I'm Numb\", \"popolarita\":10},\n" +
                                " {\"id\":\"4GCEfwd7pkpZxkjnKNkBEU\", \"anno_pubblicazione\":\"2023-03-31\", \"durata\":\"2minuti e 59secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27312ac8dcc5349be57760d33fa\", \"nome\":\"ab could u die? thx. ^_^\", \"popolarita\":13},\n" +
                                " {\"id\":\"4iM5Kr7zpLITz68gkkU5nv\", \"anno_pubblicazione\":\"2022-12-23\", \"durata\":\"2minuti e 8secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273673ec28affcefb388441d114\", \"nome\":\"NUMB\", \"popolarita\":39},\n" +
                                " {\"id\":\"4kABoj2svs7baPScsQBXsr\", \"anno_pubblicazione\":\"2019-11-01\", \"durata\":\"2minuti e 23secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273ecf59a18f05bd6e109160f7b\", \"nome\":\"Bar\", \"popolarita\":26},\n" +
                                " {\"id\":\"4LDcpmAfyAI12rvk0x72j9\", \"anno_pubblicazione\":\"2019-06-07\", \"durata\":\"2minuti e 29secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273920405c24dca60588b398976\", \"nome\":\"Bar-B-Q\", \"popolarita\":30},\n" +
                                " {\"id\":\"4nfdvzSICQaDSavscKv5t3\", \"anno_pubblicazione\":\"2023-11-03\", \"durata\":\"3minuti e 37secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27331866564a5b903800500f4ab\", \"nome\":\"Real house wibes (desperate house vibes)\", \"popolarita\":32},\n" +
                                " {\"id\":\"4r20OC2BV6r4WMXJo8djiV\", \"anno_pubblicazione\":\"2023-08-29\", \"durata\":\"2minuti e 10secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a4938ce3fc3cf84bdb92f433\", \"nome\":\"numb\", \"popolarita\":38},\n" +
                                " {\"id\":\"4SeijoBFSbtyQ9TldzUq1S\", \"anno_pubblicazione\":\"2023-04-14\", \"durata\":\"3minuti e 13secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733f4e6d9c84699463d26a1351\", \"nome\":\"Freestyle 5\", \"popolarita\":25},\n" +
                                " {\"id\":\"4Uo0NB7RbaWVcVKlZIMQVd\", \"anno_pubblicazione\":\"2023-11-17\", \"durata\":\"3minuti e 12secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735d1149124b16bd41dbd0dc7e\", \"nome\":\"Bar4Bar\", \"popolarita\":33},\n" +
                                " {\"id\":\"4vq4JRXuAzn6Uwqax4YT7F\", \"anno_pubblicazione\":\"2017-01-25\", \"durata\":\"2minuti e 34secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273cbf04183e106b1547e1fc252\", \"nome\":\"Bar Exam, Pt. 1\", \"popolarita\":11},\n" +
                                " {\"id\":\"4wR1yifTuhXf3EoJz6HVy7\", \"anno_pubblicazione\":\"2024-04-24\", \"durata\":\"4minuti e 31secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27331bda57a56e8dcb286fa1afa\", \"nome\":\"Zombie Mode (Bar Wars Cypher #12)\", \"popolarita\":21},\n" +
                                " {\"id\":\"4ydApLofM7cYWfeAQaARUw\", \"anno_pubblicazione\":\"2024-02-28\", \"durata\":\"3minuti e 19secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a4f05977ea0bf378e25b69f2\", \"nome\":\"2 MARCH\", \"popolarita\":23},\n" +
                                " {\"id\":\"4YgbOe7ToShGKAKBnH0HWy\", \"anno_pubblicazione\":\"2023-01-10\", \"durata\":\"3minuti e 15secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c72b60f24a631fa78673ea2a\", \"nome\":\"I Can't Say\", \"popolarita\":22},\n" +
                                " {\"id\":\"515MVhJ5mWAk41VfyOrezB\", \"anno_pubblicazione\":\"2018-03-06\", \"durata\":\"3minuti e 40secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27371099c9123a961f3d5a7038c\", \"nome\":\"Numb\", \"popolarita\":34},\n" +
                                " {\"id\":\"51MCL2RrlFhRXOZInd93ah\", \"anno_pubblicazione\":\"2018-08-24\", \"durata\":\"3minuti e 42secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733608dede98a937be76e3ef6b\", \"nome\":\"Numb\", \"popolarita\":31},\n" +
                                " {\"id\":\"52c1zWXAGIR4Uw8r5kexLF\", \"anno_pubblicazione\":\"2023-05-12\", \"durata\":\"1minuti e 56secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2737e33a761673c34cba89fe098\", \"nome\":\"abbi's interlude\", \"popolarita\":17},\n" +
                                " {\"id\":\"52G0pwVVKzWNFvjyq6z9xm\", \"anno_pubblicazione\":\"2024-01-30\", \"durata\":\"2minuti e 35secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733727c02c90dac49db07271f5\", \"nome\":\"SCYPE\", \"popolarita\":23},\n" +
                                " {\"id\":\"57GbKAiWcdtTP0cpTEUpVC\", \"anno_pubblicazione\":\"2017-11-08\", \"durata\":\"3minuti e 2secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27370a4c62e5aaae160fe3b73a2\", \"nome\":\"Numb4ever\", \"popolarita\":34},\n" +
                                " {\"id\":\"57oyZQdMDQOweDFj9vML0C\", \"anno_pubblicazione\":\"2023-05-19\", \"durata\":\"2minuti e 38secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27362e27beb3e39834594c0d942\", \"nome\":\"abbie\", \"popolarita\":21},\n" +
                                " {\"id\":\"595ktLpC0t9ZYhYZ1flZBF\", \"anno_pubblicazione\":\"2001-08-29\", \"durata\":\"3minuti e 28secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e90a0b242d2cc9d42d08ef40\", \"nome\":\"Bar Exam\", \"popolarita\":27},\n" +
                                " {\"id\":\"5a67rLSho75xogrQVBtkrs\", \"anno_pubblicazione\":\"2024-01-15\", \"durata\":\"4minuti e 1secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c896d3f9a54b2f59bec343f4\", \"nome\":\"Bar Shark\", \"popolarita\":19},\n" +
                                " {\"id\":\"5ABRzaj7an8yRFpIFxayiN\", \"anno_pubblicazione\":\"2024-05-02\", \"durata\":\"2minuti e 44secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273572a96d109f34d0a3b073ca2\", \"nome\":\"NUMB\", \"popolarita\":11},\n" +
                                " {\"id\":\"5Aifh9LNXmccnTqRiDxlmr\", \"anno_pubblicazione\":\"2022-07-15\", \"durata\":\"2minuti e 17secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2730a95714082b6742002b1f7f0\", \"nome\":\"Abby\", \"popolarita\":27},\n" +
                                " {\"id\":\"5CDXNn6t9D4bFtBkbtr18T\", \"anno_pubblicazione\":\"2023-04-14\", \"durata\":\"2minuti e 42secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27320340b2699fd6da07c6d0739\", \"nome\":\"Bar4Bar\", \"popolarita\":4},\n" +
                                " {\"id\":\"5CvJraMXrLtmrg9p465XXn\", \"anno_pubblicazione\":\"2024-02-02\", \"durata\":\"3minuti e 9secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27342526a870c1549a4493dc547\", \"nome\":\"4THEPAiN\", \"popolarita\":21},\n" +
                                " {\"id\":\"5D70uM4MC0Y70Wm8oDAGPP\", \"anno_pubblicazione\":\"1984-01-01\", \"durata\":\"4minuti e 45secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c92efb1a7d421f29a9fff455\", \"nome\":\"Freakshow On The Dance Floor\", \"popolarita\":21},\n" +
                                " {\"id\":\"5DpOrZhEK1QQ9HoDZWbU8E\", \"anno_pubblicazione\":\"2022-05-13\", \"durata\":\"3minuti e 4secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e9565c16b1c048ec3b19b070\", \"nome\":\"Bar Mediterraneo\", \"popolarita\":50},\n" +
                                " {\"id\":\"5DwvNb0FqBuDbzRJT0tSAK\", \"anno_pubblicazione\":\"2023-11-03\", \"durata\":\"2minuti e 9secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273aabae425d157ba233fd602a0\", \"nome\":\"Access\", \"popolarita\":26},\n" +
                                " {\"id\":\"5fZJQrFKWQLb7FpJXZ1g7K\", \"anno_pubblicazione\":\"2024-05-31\", \"durata\":\"2minuti e 51secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27360df033c8f8b17384387666a\", \"nome\":\"A Bar Song (Tipsy)\", \"popolarita\":54},\n" +
                                " {\"id\":\"5Hg9rPzGlfBsK0HJ9YRc11\", \"anno_pubblicazione\":\"2023-04-07\", \"durata\":\"3minuti e 3secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27396e5170e0e80e5697768a2f3\", \"nome\":\"Numb\", \"popolarita\":53},\n" +
                                " {\"id\":\"5hTukZXdRL8Ai6QV4nMWvJ\", \"anno_pubblicazione\":\"2024-02-22\", \"durata\":\"2minuti e 58secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27348d7573a680efa56344364c8\", \"nome\":\"BETA!\", \"popolarita\":25},\n" +
                                " {\"id\":\"5hvM2fHj9jiwVxE2CkcfR2\", \"anno_pubblicazione\":\"2023-10-09\", \"durata\":\"1minuti e 0secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735865c3955b6494d932cddc0a\", \"nome\":\"Stupid\", \"popolarita\":22},\n" +
                                " {\"id\":\"5jx3eUCNlYSGIQudnJLEwm\", \"anno_pubblicazione\":\"2023-05-12\", \"durata\":\"2minuti e 32secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2738d29f68e76de80b93b8ddc57\", \"nome\":\"Bar None\", \"popolarita\":33},\n" +
                                " {\"id\":\"5KNkdVrj5oWhxzWmckglji\", \"anno_pubblicazione\":\"2023-03-19\", \"durata\":\"2minuti e 41secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273542ffe38bede859a06b42304\", \"nome\":\"(Dangerous)\", \"popolarita\":29},\n" +
                                " {\"id\":\"5Ncwl3ECDYBbEf7vk7Irmn\", \"anno_pubblicazione\":\"2024-03-08\", \"durata\":\"3minuti e 12secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273d85ac1a15fcb3e9931f8ae31\", \"nome\":\"Bar4Bar\", \"popolarita\":25},\n" +
                                " {\"id\":\"5rdrGT068PeZfY1J32mcDn\", \"anno_pubblicazione\":\"2022-09-09\", \"durata\":\"3minuti e 18secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273fa9611350ed5620e507fb71f\", \"nome\":\"Churches and Bars\", \"popolarita\":28},\n" +
                                " {\"id\":\"5rsnfjFFkwpWWKVSYOEjoH\", \"anno_pubblicazione\":\"2024-04-11\", \"durata\":\"2minuti e 13secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c6118a050b2d962bb36824a5\", \"nome\":\"Numb\", \"popolarita\":36},\n" +
                                " {\"id\":\"5RZfy7N8mfLuABjXi6OAKT\", \"anno_pubblicazione\":\"2023-07-27\", \"durata\":\"1minuti e 57secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2731893fd3130c0908a7fd7559d\", \"nome\":\"Boom\", \"popolarita\":13},\n" +
                                " {\"id\":\"5SGihngg4WggY5Nmynjxxm\", \"anno_pubblicazione\":\"2023-09-07\", \"durata\":\"3minuti e 3secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a1721ca5ce45c7d6d1ecaa34\", \"nome\":\"Numb - SwishaHouse Remix\", \"popolarita\":15},\n" +
                                " {\"id\":\"5uBkdzRu62OVIhq3ss2mIg\", \"anno_pubblicazione\":\"2024-04-12\", \"durata\":\"2minuti e 22secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273896341c31bc6f6181073e6fc\", \"nome\":\"No Talk\", \"popolarita\":27},\n" +
                                " {\"id\":\"5XAqi952dAcUD6pd7Mwk5I\", \"anno_pubblicazione\":\"2008\", \"durata\":\"4minuti e 45secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e2e2295b2b47f6bbcbbe7265\", \"nome\":\"Picture Me\", \"popolarita\":17},\n" +
                                " {\"id\":\"5xhB62X5svVDYitg5fDe3v\", \"anno_pubblicazione\":\"2018-08-03\", \"durata\":\"3minuti e 2secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735477d4a494dc6bba81d42140\", \"nome\":\"Choose My Friends\", \"popolarita\":30},\n" +
                                " {\"id\":\"5xODaw1CaAZHZ0MwTWKUt0\", \"anno_pubblicazione\":\"2023-11-03\", \"durata\":\"4minuti e 29secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27331866564a5b903800500f4ab\", \"nome\":\"twist\", \"popolarita\":30},\n" +
                                " {\"id\":\"5XzqhbkrLsu8WKGfjayqcY\", \"anno_pubblicazione\":\"2024-03-01\", \"durata\":\"3minuti e 27secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2739a71d9a9e8963a26f50f1249\", \"nome\":\"Abnormality Dancing Girl\", \"popolarita\":22},\n" +
                                " {\"id\":\"5YJJeTheJ4XmPwlHQuKONd\", \"anno_pubblicazione\":\"2024-05-24\", \"durata\":\"4minuti e 40secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c04977aa05274eff08c11f19\", \"nome\":\"Abbas\", \"popolarita\":13},\n" +
                                " {\"id\":\"5zrVWZwzqTApMN2MyZCJQq\", \"anno_pubblicazione\":\"2024-03-29\", \"durata\":\"3minuti e 26secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273d791004d0c7533f95c4aeacc\", \"nome\":\"It's All Love\", \"popolarita\":47},\n" +
                                " {\"id\":\"65TKj6D1Ic5P28FNnXHtiU\", \"anno_pubblicazione\":\"2019-12-31\", \"durata\":\"1minuti e 51secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273bad96a969a76ff3d6dcc83e7\", \"nome\":\"Whole Lotta Money\", \"popolarita\":11},\n" +
                                " {\"id\":\"69pHMM7biztKZHCTnxnzFI\", \"anno_pubblicazione\":\"2023-01-13\", \"durata\":\"1minuti e 41secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273f450c7edb1a26ad035135708\", \"nome\":\"numb\", \"popolarita\":62},\n" +
                                " {\"id\":\"6E6V8bzTKzPkBOAxvHsUop\", \"anno_pubblicazione\":\"2023-06-09\", \"durata\":\"1minuti e 32secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2732f941601b41e684755bd8cb0\", \"nome\":\"numb (Sped Up)\", \"popolarita\":34},\n" +
                                " {\"id\":\"6GjRWL1w7OcFDj8qyfINQA\", \"anno_pubblicazione\":\"2024-04-26\", \"durata\":\"3minuti e 25secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27346084a6b10d2bacf61621f1a\", \"nome\":\"ABV\", \"popolarita\":15},\n" +
                                " {\"id\":\"6kcS27BkRzqvuo2o5l29OZ\", \"anno_pubblicazione\":\"2019-10-11\", \"durata\":\"4minuti e 21secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273f71640333a684572d5a5e5b1\", \"nome\":\"Numb\", \"popolarita\":34},\n" +
                                " {\"id\":\"6n0IB1iqZBvv7ZrsIb8Ib9\", \"anno_pubblicazione\":\"2024-02-16\", \"durata\":\"3minuti e 18secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273d376e9158f63e9a4fc5511c9\", \"nome\":\"Lana's Eyes\", \"popolarita\":17},\n" +
                                " {\"id\":\"6N6NP3tCw53nh0Wjqd4eqB\", \"anno_pubblicazione\":\"2023-09-26\", \"durata\":\"2minuti e 59secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27372de0edae25e172bf15c2908\", \"nome\":\"Numb Little Bug\", \"popolarita\":26},\n" +
                                " {\"id\":\"6oxtk5HFwiNBCEJ2z8wyeJ\", \"anno_pubblicazione\":\"2008\", \"durata\":\"4minuti e 26secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e2e2295b2b47f6bbcbbe7265\", \"nome\":\"Whoa\", \"popolarita\":22},\n" +
                                " {\"id\":\"6r1kNxx7z6dmVzV8GBcL6C\", \"anno_pubblicazione\":\"2014-01-01\", \"durata\":\"4minuti e 15secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273d4a55753958ef2043e89816a\", \"nome\":\"Abnormal\", \"popolarita\":28},\n" +
                                " {\"id\":\"6strixXzcveS6oCOajHYhi\", \"anno_pubblicazione\":\"2023-12-11\", \"durata\":\"3minuti e 10secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273285253722e9d1c90ab98bb10\", \"nome\":\"Numb\", \"popolarita\":28},\n" +
                                " {\"id\":\"6vOWtUa3Z3iQGoEZmYr9uI\", \"anno_pubblicazione\":\"2023-05-26\", \"durata\":\"2minuti e 7secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2738392371beedd8432e50dfa23\", \"nome\":\"numb myself~\", \"popolarita\":17},\n" +
                                " {\"id\":\"6wxTV2vBJNRBgJHw8VkfbW\", \"anno_pubblicazione\":\"2022-09-18\", \"durata\":\"2minuti e 10secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27339b1433834cdf3e7ccdd491e\", \"nome\":\"Abnormal Approve\", \"popolarita\":16},\n" +
                                " {\"id\":\"6XSxFVLPizXBjvRj1LSLMa\", \"anno_pubblicazione\":\"2023-06-07\", \"durata\":\"4minuti e 57secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2731e358a06d202202fadc9ad1a\", \"nome\":\"A/B\", \"popolarita\":32},\n" +
                                " {\"id\":\"6xukXoM8FBei70MxGw58uD\", \"anno_pubblicazione\":\"2024-03-18\", \"durata\":\"4minuti e 43secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2738b6133fa01dd1e875019e356\", \"nome\":\"Let Your Grip Dig In\", \"popolarita\":32},\n" +
                                " {\"id\":\"6YdFePOtOJBLn6tR1D6nUg\", \"anno_pubblicazione\":\"2023-05-31\", \"durata\":\"1minuti e 22secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2734f797470dbf4cd7674f49dbd\", \"nome\":\"abroad\", \"popolarita\":43},\n" +
                                " {\"id\":\"72GG8fGLgF1qJV1YQqavAa\", \"anno_pubblicazione\":\"2024-04-12\", \"durata\":\"1minuti e 47secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273896341c31bc6f6181073e6fc\", \"nome\":\"Outro Lounge Bar\", \"popolarita\":26},\n" +
                                " {\"id\":\"75Fa1uUHlHCax29UOyJMaU\", \"anno_pubblicazione\":\"2024-03-25\", \"durata\":\"1minuti e 48secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27302ace78c6fccfba6494bf082\", \"nome\":\"Bar\", \"popolarita\":24},\n" +
                                " {\"id\":\"77eaQ9mlp6u6RpK9H1OcTt\", \"anno_pubblicazione\":\"2023-09-29\", \"durata\":\"2minuti e 19secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273d8442067709246456e1b6263\", \"nome\":\"Bentley\", \"popolarita\":22},\n" +
                                " {\"id\":\"7bOQ9cB6HjBF7MGkVZahPU\", \"anno_pubblicazione\":\"2023-05-19\", \"durata\":\"2minuti e 55secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273cee0ec0e49e315b726c2f623\", \"nome\":\"NOCD\", \"popolarita\":28},\n" +
                                " {\"id\":\"7ebgAxd8GI35JA6PvwnIoG\", \"anno_pubblicazione\":\"2022-04-30\", \"durata\":\"2minuti e 43secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273977cdab9d619de0a5d951a9e\", \"nome\":\"Numb Little Bug\", \"popolarita\":40},\n" +
                                " {\"id\":\"7EZSvOOlvauQDamANeoSOy\", \"anno_pubblicazione\":\"2023-06-02\", \"durata\":\"2minuti e 19secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735effcc2a2cd3772b599b3eb3\", \"nome\":\"Bar\", \"popolarita\":31},\n" +
                                " {\"id\":\"7ghbiZ0Imvdfm7Fyml3sdO\", \"anno_pubblicazione\":\"2022-07-09\", \"durata\":\"2minuti e 29secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273205644213720c5f91dccb164\", \"nome\":\"A B C\", \"popolarita\":12},\n" +
                                " {\"id\":\"7Hf3Znb9Dj2Gxkk2uz3TYB\", \"anno_pubblicazione\":\"2020-12-07\", \"durata\":\"1minuti e 35secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e4c93f62f66636d06232d7c4\", \"nome\":\"Bar None\", \"popolarita\":18},\n" +
                                " {\"id\":\"7JIamGYiff5FiiyEUArQ0H\", \"anno_pubblicazione\":\"2023-08-29\", \"durata\":\"2minuti e 17secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2738113a0c0a8259392e1d2d234\", \"nome\":\"Focus\", \"popolarita\":15},\n" +
                                " {\"id\":\"7lqx76e4X3YM1wnVyPsNAn\", \"anno_pubblicazione\":\"2024-05-15\", \"durata\":\"3minuti e 4secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27352b27597745d05edc4aa4595\", \"nome\":\"Numb\", \"popolarita\":17},\n" +
                                " {\"id\":\"7ncBoRQ9h6MC1ViYZs9gPe\", \"anno_pubblicazione\":\"2023-04-07\", \"durata\":\"1minuti e 55secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a116a1ad26ae27e0f13a9535\", \"nome\":\"Numb (lofi)\", \"popolarita\":23},\n" +
                                " {\"id\":\"7nnxF7L3aTcByFv58FxXAJ\", \"anno_pubblicazione\":\"2022-11-12\", \"durata\":\"2minuti e 47secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27347da187ecea0f27557e9bd5c\", \"nome\":\"numb\", \"popolarita\":23},\n" +
                                " {\"id\":\"7oHsQiZ1O4RRUYJGwsbGnM\", \"anno_pubblicazione\":\"2021-04-16\", \"durata\":\"2minuti e 40secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27323195cc21796a2c299a9a110\", \"nome\":\"Bar Whore\", \"popolarita\":27},\n" +
                                " {\"id\":\"7poNiWA9s4xSJeVmTUb7Sl\", \"anno_pubblicazione\":\"2024-03-15\", \"durata\":\"2minuti e 22secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273041ab6a1dcbc1d89f6b8b269\", \"nome\":\"No Talk\", \"popolarita\":25},\n" +
                                " {\"id\":\"7qZj5yB6KpFECLBGpnF6SU\", \"anno_pubblicazione\":\"2024-05-02\", \"durata\":\"3minuti e 2secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a6d003f1d488450df36e8808\", \"nome\":\"Zero Chill\", \"popolarita\":31},\n" +
                                " {\"id\":\"7uHtasPjnmj0AHc6NtO0hi\", \"anno_pubblicazione\":\"2023-06-16\", \"durata\":\"3minuti e 11secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273803c4f78c9e4040fd5eee209\", \"nome\":\"Bars and Churches\", \"popolarita\":17},\n" +
                                " {\"id\":\"7wyBBN7lprezi9jvWMRCNN\", \"anno_pubblicazione\":\"2011-04-05\", \"durata\":\"5minuti e 20secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27331770acc5bf39b3d2875fa64\", \"nome\":\"#LTM\", \"popolarita\":19},\n" +
                                " {\"id\":\"7zuuWgubLE6JQmPv49XPxd\", \"anno_pubblicazione\":\"2023-12-25\", \"durata\":\"2minuti e 6secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733795d3f33c8f242cb1eaf494\", \"nome\":\"Life\", \"popolarita\":22},\n" +
                                " {\"id\":\"7zZvVZ323QZQpdSX035mqc\", \"anno_pubblicazione\":\"2023-10-27\", \"durata\":\"2minuti e 37secondi\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27390d7385c27ec6207bf72c0f4\", \"nome\":\"Numb\", \"popolarita\":10}]"

                        val tracks = Gson().fromJson(file,Array<Track>::class.java)
                        dao.insertAllTracks(tracks)
                        print("cc")
                    }

                    private suspend fun populateArtisti(dao: ArtistiDao) {
                                val file = "[{\"id\":\"00eXgMv8c9rsPVyhs7Lyu0\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27340bc03af5cb8b3e31137bd6e\", \"nome\":\"Baybe Heem\", \"popolarita\":16},\n" +
                                        " {\"id\":\"00i4PzAbt6fpwgBqdtSP0q\", \"genere\":\"chillhop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb44209978f07a72b5480f17d2\", \"nome\":\"anbuu\", \"popolarita\":31},\n" +
                                        " {\"id\":\"01crEa9G3pNpXZ5m7wuHOk\", \"genere\":\"alternative metal,christian alternative rock,christian rock,post-grunge,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb88549bbfac1d49a197b2af41\", \"nome\":\"Red\", \"popolarita\":52},\n" +
                                        " {\"id\":\"05wYv5S3PqoZu9tHE1vDFw\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb234d563a00cbeaf3331582e4\", \"nome\":\"Bylu!\", \"popolarita\":9},\n" +
                                        " {\"id\":\"06ZnpDN0sw3qvEh6gLM1X1\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebef40b2c2bf481a1b7b366982\", \"nome\":\"Abby The Spoon Lady\", \"popolarita\":28},\n" +
                                        " {\"id\":\"07sRXwh1InLMCvXQgR1CqQ\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"Ab Da Jet\", \"popolarita\":30},\n" +
                                        " {\"id\":\"08X2snEY9iTdBPSo08CKU0\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebaba2abbbd17118ef7a4acbb9\", \"nome\":\"Baw\$.\", \"popolarita\":7},\n" +
                                        " {\"id\":\"0AFZWmw2cnyf2OYNM8dThu\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb96f7e7edab350dccee3fcb69\", \"nome\":\"BM0R\", \"popolarita\":17},\n" +
                                        " {\"id\":\"0bTV4EUB2amSUF0k8Ri6b2\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273ac5d9afc9ae7721cfe17424f\", \"nome\":\"H_H\", \"popolarita\":29},\n" +
                                        " {\"id\":\"0buIN8i27CrcKjvLYdIT2h\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735a91aba8cafb3d4270172cb3\", \"nome\":\"Abnaji\", \"popolarita\":26},\n" +
                                        " {\"id\":\"0D98V0wg9XOFT6flzCNZGP\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebaf827e00b32dbe3bb3fdf771\", \"nome\":\"B.A.\", \"popolarita\":21},\n" +
                                        " {\"id\":\"0DJJrg8eUagEWZXFgwKJfM\", \"genere\":\"gymcore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb818ea6c591f2d03ab9d9588e\", \"nome\":\"ENMY\", \"popolarita\":46},\n" +
                                        " {\"id\":\"0fGVuq5ed21pM7iWwTcMyk\", \"genere\":\"hel,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb23f91cfef09d0f0a92bbaf0f\", \"nome\":\"Amy Lee\", \"popolarita\":53},\n" +
                                        " {\"id\":\"0Foxbw02DLnmU3y2lAWbQ0\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2732c7b08c61c8993806fbbdb66\", \"nome\":\"B-Kane\", \"popolarita\":24},\n" +
                                        " {\"id\":\"0g9vAlRPK9Gt3FKCekk4TW\", \"genere\":\"conscious hip hop,hip hop,rap,underground hip hop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb313ba2f006e069072e6ed486\", \"nome\":\"Ab-Soul\", \"popolarita\":57},\n" +
                                        " {\"id\":\"0Gw3a3BkWLwsMLFbOBmo6Q\", \"genere\":\"alternative metal,industrial metal,nu metal,post-grunge,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebcad2a98065e13720ce622c6c\", \"nome\":\"Cold\", \"popolarita\":47},\n" +
                                        " {\"id\":\"0H942IkjXv9bjx5OxoG7kh\", \"genere\":\"krushclub,sigilkore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb5cd7b4599d10ffc35643665d\", \"nome\":\"jnhygs\", \"popolarita\":67},\n" +
                                        " {\"id\":\"0HhLNo5pI32wCYNKqpXuCD\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb174ff4bedd041ec63bc6f879\", \"nome\":\"Abby Webster\", \"popolarita\":28},\n" +
                                        " {\"id\":\"0HoqUfZJ4Q968iu3M20RQ9\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273f81ae247364cde8fd5d4a437\", \"nome\":\"Bakhaki\", \"popolarita\":25},\n" +
                                        " {\"id\":\"0k52cXAjNIDjZOE1WDEV93\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb1a742b680eb3b017ca3776c2\", \"nome\":\"NOME.\", \"popolarita\":35},\n" +
                                        " {\"id\":\"0kl49aZd1paLkcwijmo6vF\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb0ff290552981e174e3eec9cb\", \"nome\":\"B.A.N.\", \"popolarita\":27},\n" +
                                        " {\"id\":\"0KMlyqeocN19K1DTl4TpzQ\", \"genere\":\"chill dream pop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebcc08799ef016cbd3838e08fe\", \"nome\":\".com\", \"popolarita\":35},\n" +
                                        " {\"id\":\"0KWgRtUbQXSiICkWp7g213\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebaba24749d966df751cf799d6\", \"nome\":\"No_4mat\", \"popolarita\":38},\n" +
                                        " {\"id\":\"0L6SLnHkOyY2ajoETxWBq5\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebd3372ef45c5cae433aaf79f9\", \"nome\":\"wound\", \"popolarita\":27},\n" +
                                        " {\"id\":\"0nctkXQ44dcIxZ06zEGtRj\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb274d8bd3b931cfe9c22a3af0\", \"nome\":\"badp\", \"popolarita\":25},\n" +
                                        " {\"id\":\"0osA8WHktzmv6QnWfbUyha\", \"genere\":\"corecore,sped up,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb9cb20deb4a928c35b745aa5b\", \"nome\":\"Goomp\", \"popolarita\":53},\n" +
                                        " {\"id\":\"0tLfqk1HbEtVZ94GJKVqWN\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"B Rock\", \"popolarita\":21},\n" +
                                        " {\"id\":\"0TLWRToGoCnzTLTEcCdoJc\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb107bd31787358b718da1bf81\", \"nome\":\"BXTHEFCKUP\", \"popolarita\":24},\n" +
                                        " {\"id\":\"0Wv385aRvyuQLODQLsT8Zl\", \"genere\":\"dark cabaret,steampunk,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb91ced14c589646ea578baa05\", \"nome\":\"Abney Park\", \"popolarita\":31},\n" +
                                        " {\"id\":\"0zVeDWDqdz4sz23IdRoE3m\", \"genere\":\"sped up,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb9ec9ab628dbbb1ffe103ed36\", \"nome\":\"ONIBI\", \"popolarita\":53},\n" +
                                        " {\"id\":\"10zsHcOcO047KmhOhi57iL\", \"genere\":\"dariacore,mashcore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb240f810a37b2d53e5a627693\", \"nome\":\"xaev\", \"popolarita\":30},\n" +
                                        " {\"id\":\"11260Smss09lExMicvRPeO\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebad1419417fb4aab8b4fa556f\", \"nome\":\"Abby Roberts\", \"popolarita\":32},\n" +
                                        " {\"id\":\"15jmHBfG60gSHr2ZSU3B6z\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebe4529bf5369224b514721559\", \"nome\":\"nohpetS\", \"popolarita\":26},\n" +
                                        " {\"id\":\"165ZgPlLkK7bf5bDoFc6Sb\", \"genere\":\"alternative metal,funk metal,nu metal,post-grunge,rap metal,rock,\", \"immagine\":\"https://i.scdn.co/image/4654955890cb62a6abe9daadfd2b3ecdee8036e4\", \"nome\":\"Limp Bizkit\", \"popolarita\":73},\n" +
                                        " {\"id\":\"18AWDR2N9U6HkTZ6wgmbRV\", \"genere\":\"scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebac819fd4541ee3379a9433e3\", \"nome\":\"kojo\", \"popolarita\":50},\n" +
                                        " {\"id\":\"1aH87CIYgz3YJCJbCCaNsU\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebd33f90d1ff5d48338cb27d93\", \"nome\":\"BaKu\", \"popolarita\":15},\n" +
                                        " {\"id\":\"1AyGTTRYucikxKSCBtWwN0\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eba86b818fb097248626868450\", \"nome\":\"Abrii\", \"popolarita\":17},\n" +
                                        " {\"id\":\"1crlEwxm5dcQjCcgCJEyqg\", \"genere\":\"neo r&b,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb8970fe187958931a58f44d2c\", \"nome\":\"Abhi//Dijon\", \"popolarita\":32},\n" +
                                        " {\"id\":\"1cZAiuSdn7vicAxGwX3VjY\", \"genere\":\"scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb63372d45eecbddcc434d1808\", \"nome\":\"03osc\", \"popolarita\":36},\n" +
                                        " {\"id\":\"1EXymL8zEdU5g62GiBfq6D\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebe31383c77f75656c47259f44\", \"nome\":\"b/t\", \"popolarita\":35},\n" +
                                        " {\"id\":\"1gUi2utSbJLNPddYENJAp4\", \"genere\":\"desi hip hop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3e58bad8955afb9fe962227c\", \"nome\":\"Abhi The Nomad\", \"popolarita\":48},\n" +
                                        " {\"id\":\"1IJgsVtvSTjBp9Wwi0ajII\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb29f68945828d921675aefea7\", \"nome\":\"B A K\", \"popolarita\":14},\n" +
                                        " {\"id\":\"1iUrwP4rauzWYIYxFcYJ9Z\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebeb527829d47856d96c1229dc\", \"nome\":\"Abby Boy\", \"popolarita\":29},\n" +
                                        " {\"id\":\"1j6WlpKlErRMXTjGKez8oJ\", \"genere\":\"aesthetic rap,\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27315b56ec363fedd67ca673d8f\", \"nome\":\"Basco\", \"popolarita\":41},\n" +
                                        " {\"id\":\"1mhpQdXVCnq31AwzgCPxK9\", \"genere\":\"chill house,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebf84200ae479ec3021e63d1c2\", \"nome\":\"BAF\", \"popolarita\":35},\n" +
                                        " {\"id\":\"1nf7xecdOfiFT05Aa7VFk3\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"Abby Mueller\", \"popolarita\":27},\n" +
                                        " {\"id\":\"1NFEqe4FKmf4nRScXBKfk6\", \"genere\":\"rap cristiano,trap cristiano,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebb8582cc9fe1dff7e507bec0d\", \"nome\":\"Abdi\", \"popolarita\":37},\n" +
                                        " {\"id\":\"1ooV8YZC1KbpEcrmI8WH0F\", \"genere\":\"pop soul,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebc7c93edb239b99c22a84bdd9\", \"nome\":\"Yebba\", \"popolarita\":63},\n" +
                                        " {\"id\":\"1Q4llfcvp4DD8PV69Eaexq\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebea5f44e903bcc6d364c68598\", \"nome\":\"n9neful\", \"popolarita\":28},\n" +
                                        " {\"id\":\"1S82w4yw9TYIHZ889mPPaW\", \"genere\":\"bedroom r&b,krushclub,sped up,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb777f2bab9e5f1e343c3126d4\", \"nome\":\"Kanii\", \"popolarita\":66},\n" +
                                        " {\"id\":\"1scrvreYQVFmaZTPUGUxdG\", \"genere\":\"scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebf0f45ac1ba3173452f4c64d6\", \"nome\":\"Bayymack\", \"popolarita\":37},\n" +
                                        " {\"id\":\"1sio0tKvSyOglNuysoIhPq\", \"genere\":\"phonk,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7c690061cd53a0b706a27bb0\", \"nome\":\"DOM\", \"popolarita\":23},\n" +
                                        " {\"id\":\"1TesnsLXa8HzOB67nj6BnV\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb0384b5e93c0050499fd94ce8\", \"nome\":\"d9m\", \"popolarita\":22},\n" +
                                        " {\"id\":\"1TV9PqYrCs2SLsPlYGSEu6\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb5623e7275d1739580c74b7c4\", \"nome\":\"abbytheoboist\", \"popolarita\":15},\n" +
                                        " {\"id\":\"1tWBEFYkWnb4CXo8Edz2u2\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebb389c4534f001601216d3653\", \"nome\":\"A\\\\RT\", \"popolarita\":17},\n" +
                                        " {\"id\":\"1UdQqCUR7RwB9YYJONwbdM\", \"genere\":\"alternative metal,nu metal,post-grunge,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb403d2145e451b333a221bdcd\", \"nome\":\"Trapt\", \"popolarita\":55},\n" +
                                        " {\"id\":\"1vdaenasA3QoZWM7qCra59\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb5dbb482f22ac4bba8a6f29d0\", \"nome\":\"BapeBaby\", \"popolarita\":22},\n" +
                                        " {\"id\":\"1WJ1xPyOeMJcam5F2wKqwM\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7df5a985f02d2455db4ea590\", \"nome\":\"AB\", \"popolarita\":14},\n" +
                                        " {\"id\":\"1WkLzPQrfUw0l6skY3KGNf\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb59d7383d8a2b3362bc9222ca\", \"nome\":\"721gusto\", \"popolarita\":30},\n" +
                                        " {\"id\":\"1wnKaN5GmEJQjhhVvzGt89\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eba4cf5c11be1b4aadac82d88a\", \"nome\":\"Abacows\", \"popolarita\":36},\n" +
                                        " {\"id\":\"1ZC6S2wEd4CuG2bclx32HP\", \"genere\":\"deep freestyle,freestyle,\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27327fa2b4e557202ffc7ab4a33\", \"nome\":\"Aby\", \"popolarita\":13},\n" +
                                        " {\"id\":\"22PN8q6f33B3WpZI7u6CbG\", \"genere\":\"philly rap,\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273fc87b90c3f5bf548308cf9aa\", \"nome\":\"AR-AB\", \"popolarita\":29},\n" +
                                        " {\"id\":\"22qJESCCU7gA3Co2BloawG\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2737fd4ddff99ae23bc3937660c\", \"nome\":\"Baxoota\", \"popolarita\":22},\n" +
                                        " {\"id\":\"28tk1PiLYWjkVr0fxlleKP\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"BB\", \"popolarita\":29},\n" +
                                        " {\"id\":\"2A7ui0VW1lh1JxYJMuW0b0\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebca417fb3bdb77c4322520394\", \"nome\":\"Music.com\", \"popolarita\":24},\n" +
                                        " {\"id\":\"2anlj4dNUn9I1VxxmjRHRx\", \"genere\":\"glitchcore,hyperpop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7817dfe1b5491576b18423c4\", \"nome\":\"Numl6ck\", \"popolarita\":41},\n" +
                                        " {\"id\":\"2Ape299mfk1SxfnAc8gUZq\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebfe452e20d416f7330afd2233\", \"nome\":\"anon2999111222\", \"popolarita\":22},\n" +
                                        " {\"id\":\"2bsv2IjuvKNhdxsDk6bKed\", \"genere\":\"sigilkore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb8877f39904a974abbd014258\", \"nome\":\"\\u2020w!n\", \"popolarita\":26},\n" +
                                        " {\"id\":\"2cLXVpCf7nREINxaKdjMHQ\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273737d260f379bf508f2bc02cd\", \"nome\":\"Tanto Faz O Nome Do Artista\", \"popolarita\":0},\n" +
                                        " {\"id\":\"2D8UzkIVdg1bj0Iqz6gjk0\", \"genere\":\"urbano espanol,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3a139ff857bc44d9be6bfaa4\", \"nome\":\"Abhir Hathi\", \"popolarita\":53},\n" +
                                        " {\"id\":\"2EGZz4LN62i10dsBLGlsbD\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"B.A. Barakus\", \"popolarita\":9},\n" +
                                        " {\"id\":\"2fWukyNksCO4foi0KYnRJy\", \"genere\":\"sigilkore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebdc675f5aaa44176196bac26d\", \"nome\":\"Nuvfr\", \"popolarita\":47},\n" +
                                        " {\"id\":\"2GAgYM9oyW5EkUGJonaree\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebaf68829ab0daf6a721d3c470\", \"nome\":\"Abby Paradise\", \"popolarita\":13},\n" +
                                        " {\"id\":\"2gie1bU1LwnxdFAJoTLjzT\", \"genere\":\"hip pop,\", \"immagine\":\"https://i.scdn.co/image/86bca3cf6660bf05f14560a2d0c9c0fb3ecdc8ee\", \"nome\":\"Loon\", \"popolarita\":52},\n" +
                                        " {\"id\":\"2GxGee63FW2XFbmhdpfndO\", \"genere\":\"slowed and reverb,\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2733404124d82d019f5fd2207e4\", \"nome\":\"Abyss Sounds\", \"popolarita\":39},\n" +
                                        " {\"id\":\"2hlo7e04n5T55WoJVs71XC\", \"genere\":\"sadcore,zoomergaze,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eba3d85b3fcf802406a0fdadcc\", \"nome\":\"link3\", \"popolarita\":34},\n" +
                                        " {\"id\":\"2Iu7bGRaGHmvTuVWp8wMzX\", \"genere\":\"scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb67db30550ee68cc44b33570b\", \"nome\":\"Pr\\u00f6z\", \"popolarita\":53},\n" +
                                        " {\"id\":\"2ivhM1JBhuO2FlwEnOBJpU\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb63b6f65cf466f426d65554ce\", \"nome\":\"BapeOGT's\", \"popolarita\":21},\n" +
                                        " {\"id\":\"2La1a4urnpOzhGxRs8Udvb\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebae95b946f20b82f3623a1382\", \"nome\":\"NOH.\$\", \"popolarita\":22},\n" +
                                        " {\"id\":\"2lA6dTogbqhg0oePC5mlbM\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb59228925a4cdce58ff84be9a\", \"nome\":\"Cash!*\", \"popolarita\":51},\n" +
                                        " {\"id\":\"2lgOYTyFnSmDh9r1JisN8Z\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"B.A.\", \"popolarita\":8},\n" +
                                        " {\"id\":\"2LwlPBOoq9EqTOmKi4lJ2n\", \"genere\":\"gen z singer-songwriter,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb2911638ecd7bd0eb82624f7c\", \"nome\":\"Abby Cates\", \"popolarita\":36},\n" +
                                        " {\"id\":\"2mLeBg1K7SP9d1v5hv5oXi\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273c8c5f19b26bb2f78870946d6\", \"nome\":\"Nonon\", \"popolarita\":30},\n" +
                                        " {\"id\":\"2NSBzBPu05xNzAUv10mdTc\", \"genere\":\"glitchcore,hyperpop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3f1f9fb0baade342b28ac223\", \"nome\":\"nomu.\", \"popolarita\":24},\n" +
                                        " {\"id\":\"2O9h842bA3mNWysGtYSbWj\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb9281cd95ee3ec7e62154c95c\", \"nome\":\"Abyss\", \"popolarita\":22},\n" +
                                        " {\"id\":\"2Pfv2w8a20xzC7Dr7QXRqM\", \"genere\":\"alternative metal,industrial metal,nu metal,rap metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb95146ec994f65a11f75c9a85\", \"nome\":\"Mudvayne\", \"popolarita\":57},\n" +
                                        " {\"id\":\"2SPIThivWqjfsjlYPNxYX9\", \"genere\":\"dark r&b,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb74d2d2da4dceaa6c72f51e30\", \"nome\":\"Limi\", \"popolarita\":55},\n" +
                                        " {\"id\":\"2UXnSoWQ8fW3iaDLKzoLwk\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebe18867a7c064c2edb6108656\", \"nome\":\"Ideas By Ab\", \"popolarita\":16},\n" +
                                        " {\"id\":\"2VYQTNDsvvKN9wmU5W7xpj\", \"genere\":\"alternative metal,hard rock,industrial,industrial metal,industrial rock,nu metal,post-grunge,rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb9e2dae585750a8d6055f4e35\", \"nome\":\"Marilyn Manson\", \"popolarita\":67},\n" +
                                        " {\"id\":\"2xiIXseIJcq3nG7C8fHeBj\", \"genere\":\"alternative metal,canadian metal,canadian rock,nu metal,post-grunge,rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3626be769b238e2a35010613\", \"nome\":\"Three Days Grace\", \"popolarita\":72},\n" +
                                        " {\"id\":\"2Xq5FRwxg7YunNg6mkUPEI\", \"genere\":\"glitchcore,scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb48ce5920856e8d6d71026cf2\", \"nome\":\"Omu\", \"popolarita\":26},\n" +
                                        " {\"id\":\"2ym8zoBmNH1nYRnyHUVbL8\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"AB\", \"popolarita\":29},\n" +
                                        " {\"id\":\"30qGwXnygZNCzxjN7QsIR0\", \"genere\":\"glitch pop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb6ccba4d698716e99d31a6cc1\", \"nome\":\"BABii\", \"popolarita\":27},\n" +
                                        " {\"id\":\"32YLek6Vq1yvZDSzm8tLTS\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb8fd9a0c5e1427495d930fab7\", \"nome\":\"JJOZlAH\", \"popolarita\":36},\n" +
                                        " {\"id\":\"3A0efCVHGSzzE2LoCKCiV4\", \"genere\":\"sped up,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebca388707fc9a97b78c4c829d\", \"nome\":\"D4WN\", \"popolarita\":35},\n" +
                                        " {\"id\":\"3AA61bsylUn9mWt1W5vlry\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273a1e5022c6254d4d1202611fe\", \"nome\":\"B.A.\", \"popolarita\":6},\n" +
                                        " {\"id\":\"3aED0Zz9rG8yaikbA4CDTO\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27324447231dcb48572f83d0bcb\", \"nome\":\"Baum Ditres\", \"popolarita\":23},\n" +
                                        " {\"id\":\"3aqu3b1cM9PwsdAvU64bOU\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273eb677d4f2d75a3fd590a903e\", \"nome\":\"b6ch\", \"popolarita\":27},\n" +
                                        " {\"id\":\"3bJWbzkh2WK8JRLgmflcwk\", \"genere\":\"sigilkore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebdbaddff43462c572b34a897a\", \"nome\":\"*67\", \"popolarita\":40},\n" +
                                        " {\"id\":\"3BYJl9McCY35iO9F0KHoiz\", \"genere\":\"drift phonk,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb9e18dfb37992df6717fc5785\", \"nome\":\"Luga\", \"popolarita\":49},\n" +
                                        " {\"id\":\"3dAWhnAXnvWmutXcXGyrQn\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebc460018389decfe3d875a074\", \"nome\":\"Abyss Explorer\", \"popolarita\":29},\n" +
                                        " {\"id\":\"3e295I56TT28Wt0BMl54Ik\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebae183c7d76d445999e1301b4\", \"nome\":\"rl\", \"popolarita\":28},\n" +
                                        " {\"id\":\"3E2CPSMNaUMT0OosI4ogam\", \"genere\":\"scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb62f848c78c432f07f351f812\", \"nome\":\"nami\", \"popolarita\":33},\n" +
                                        " {\"id\":\"3h9S7RW1QR2UAHOx8MscnK\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"Abi la k'lidad\", \"popolarita\":15},\n" +
                                        " {\"id\":\"3K1AAnD5alr3OR8SClyBmc\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb0d5d3d14090ec359255fb58b\", \"nome\":\"BazukaBeko\", \"popolarita\":39},\n" +
                                        " {\"id\":\"3KN85BxqOtEPm1Tp6tFrPv\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eba47d8772d3d5dde449ba9535\", \"nome\":\"Lunko\", \"popolarita\":29},\n" +
                                        " {\"id\":\"3n4ersmDo55xV4fPSCKpXb\", \"genere\":\"alternative metal,industrial metal,nu metal,post-grunge,rap metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7d99949b3f26c9eb73a5a367\", \"nome\":\"Adema\", \"popolarita\":41},\n" +
                                        " {\"id\":\"3NLkribKVSOwNM7pLa8tVI\", \"genere\":\"krushclub,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb712bdad56afceffaaf88b020\", \"nome\":\"bxnji\", \"popolarita\":37},\n" +
                                        " {\"id\":\"3qNW8siJh47TfyMh1Yn9zZ\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb49ca7bcf55fd58bd2da7a40c\", \"nome\":\"DJ Abyss\", \"popolarita\":26},\n" +
                                        " {\"id\":\"3QUOtWgmuxFyae4C0Q0thd\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb2e55922ed7b80e66863f3345\", \"nome\":\"ABIR\", \"popolarita\":36},\n" +
                                        " {\"id\":\"3rJ3JaEwzwefdZMinCtdPI\", \"genere\":\"ambient lo-fi,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebbc5bd3481b0e338fdb040d7f\", \"nome\":\"nowt\", \"popolarita\":39},\n" +
                                        " {\"id\":\"3RNrq3jvMZxD9ZyoOZbQOD\", \"genere\":\"alternative metal,funk metal,hard rock,nu metal,post-grunge,rap metal,rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb29af2ffb6f4ddd6324f878bc\", \"nome\":\"Korn\", \"popolarita\":74},\n" +
                                        " {\"id\":\"3s6Gdmnv9MuQNX2E7i5EMr\", \"genere\":\"scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb21525ee467d2aef501ce9775\", \"nome\":\"2504\", \"popolarita\":36},\n" +
                                        " {\"id\":\"3SdkyJaXVliadve8o0W5HR\", \"genere\":\"pink noise,scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7a67f7d015fee331c4031dd1\", \"nome\":\"BAEBY ALEX\", \"popolarita\":31},\n" +
                                        " {\"id\":\"3VhKwjUzCzgIJAzpjMBXmQ\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb669f639f2070f23028dc5463\", \"nome\":\"Rarin\", \"popolarita\":50},\n" +
                                        " {\"id\":\"3yjHyjGJ69E2Ru1HyO7GsQ\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3444e30e5bded9e1dd429647\", \"nome\":\"AbYagi\", \"popolarita\":29},\n" +
                                        " {\"id\":\"3yz2mI7OSrj7ZeBaFTNCoI\", \"genere\":\"pluggnb,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb2d210443fe2b65f4edbbcbc8\", \"nome\":\"Xals!\", \"popolarita\":26},\n" +
                                        " {\"id\":\"3ZGHe6FlXVjixezPHFn3c2\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb813c218dcb5e59abee97cf00\", \"nome\":\"Noah Nolastname\", \"popolarita\":20},\n" +
                                        " {\"id\":\"3ZQEU2arcWXSv3oz8hwWBK\", \"genere\":\"nu metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb0f71a8be8274bf25cf6b91cd\", \"nome\":\"CKY\", \"popolarita\":47},\n" +
                                        " {\"id\":\"40jlY1amdIaVtPMKjdBj7x\", \"genere\":\"corecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb92cd7e70952bacdb330f4b0f\", \"nome\":\"ciaffa\", \"popolarita\":55},\n" +
                                        " {\"id\":\"41PE0deubI6MpwYruSEWHG\", \"genere\":\"krushclub,scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb77111acc42c13f5b0d6fc583\", \"nome\":\"d3r\", \"popolarita\":55},\n" +
                                        " {\"id\":\"41Q0HrwWBtuUkJc7C1Rp6K\", \"genere\":\"alternative metal,alternative rock,funk metal,funk rock,nu metal,pop rock,rap rock,reggae fusion,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb40d6cf99f1e4c6c8ad47411f\", \"nome\":\"311\", \"popolarita\":61},\n" +
                                        " {\"id\":\"43sZBwHjahUvgbx1WNIkIz\", \"genere\":\"alternative metal,nu metal,post-grunge,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb0d86c8971cc4d6fa63d58bae\", \"nome\":\"Creed\", \"popolarita\":68},\n" +
                                        " {\"id\":\"47lZtmK7Gy8c9LkJLUpjJz\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb6b267aa0ad8cff3ebee5632e\", \"nome\":\"GUTZ\", \"popolarita\":39},\n" +
                                        " {\"id\":\"4aej3kKLxSLM0WauTSfZ7k\", \"genere\":\"modern indie pop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebe851df2aad884eac7e7346cd\", \"nome\":\"Abby Sage\", \"popolarita\":43},\n" +
                                        " {\"id\":\"4fCjBVKHNmYXyZhQPNnmsp\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2737cec0b4b176092401456f404\", \"nome\":\"Bunny P\", \"popolarita\":21},\n" +
                                        " {\"id\":\"4g8Ae41SII3GhoJkpGQTBn\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb5c50b4a2f5ca50c29e2ee4f3\", \"nome\":\"o96o\", \"popolarita\":33},\n" +
                                        " {\"id\":\"4G9E0wxWagvSV3hfGucuzb\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273590cd3bf137324d8d53bebb6\", \"nome\":\"noirbaby\", \"popolarita\":25},\n" +
                                        " {\"id\":\"4hFScNSgSkApFw193xlyH7\", \"genere\":\"alternative metal,industrial metal,nu metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebc8ea92dcdf679b9114e34bc7\", \"nome\":\"Flaw\", \"popolarita\":43},\n" +
                                        " {\"id\":\"4iL7LWR5sSptkGZhAUnFb6\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb240db888225bd8cc7e15f7ad\", \"nome\":\"NISMO.EXE\", \"popolarita\":33},\n" +
                                        " {\"id\":\"4mCaZaCbYZ89khuj1klYB5\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb76918e2cfc73e46903d2885d\", \"nome\":\"BEXSTMXDE\", \"popolarita\":36},\n" +
                                        " {\"id\":\"4nXOZlYoAD67hF9aUEncMY\", \"genere\":\"cloud rap,dark trap,miami hip hop,underground hip hop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb03f6ba25a1f4441493be323b\", \"nome\":\"Pouya\", \"popolarita\":66},\n" +
                                        " {\"id\":\"4P0eS4QK9qeUhqrapGsJKS\", \"genere\":\"electronic rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb38d63710f714dae7fc30757d\", \"nome\":\"Chester Bennington\", \"popolarita\":38},\n" +
                                        " {\"id\":\"4pAQdPzxOMGNpkfNTNauua\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb80aa8c35743964f6c38abf13\", \"nome\":\"\$\\u00a3\\u20ac\", \"popolarita\":25},\n" +
                                        " {\"id\":\"4q7Td1MO6rNg3UCvqrzz1k\", \"genere\":\"modern indie folk,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb33d97700e98f0bfa592c5248\", \"nome\":\"Abby Holliday\", \"popolarita\":34},\n" +
                                        " {\"id\":\"4re8pKS4a0SGu4PS5wDp8X\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27307b02f27626977cdc26e648c\", \"nome\":\"BAV\", \"popolarita\":8},\n" +
                                        " {\"id\":\"4RRmL81NhhouCKGo8I6GaU\", \"genere\":\"dream plugg,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebba05a30c28475fe695062531\", \"nome\":\"ADTurnUp\", \"popolarita\":50},\n" +
                                        " {\"id\":\"4U0xwGUIJSrolvTcLnI8Zq\", \"genere\":\"aesthetic rap,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebb769def7e465a86578775beb\", \"nome\":\"IMIS!\", \"popolarita\":35},\n" +
                                        " {\"id\":\"4UjCKj5w0lfjIsUKbyVSu2\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb6c375d93f4430bc2390649a2\", \"nome\":\"BavySanya\", \"popolarita\":11},\n" +
                                        " {\"id\":\"4UYsrMSqpG1Sgj2TFesI41\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb33e381351de34e4182c085c8\", \"nome\":\"BaBa\", \"popolarita\":31},\n" +
                                        " {\"id\":\"4VgMw1dIYs1LkJH4H22jQf\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273986f8460575b93449ed7af8b\", \"nome\":\"BjAllinCash\", \"popolarita\":25},\n" +
                                        " {\"id\":\"4wClUlz7cRCjvnPO0ifZmb\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b27311b0e31aeb045bdac2f97fc1\", \"nome\":\"Nom de Pomme\", \"popolarita\":19},\n" +
                                        " {\"id\":\"4XicqgtdbuyZrBmqlEZdVi\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb48167b10b66d1a471ce8799d\", \"nome\":\"Abi Horne\", \"popolarita\":22},\n" +
                                        " {\"id\":\"4yH7JCMn22ImIt9i7gJI9G\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb0a1181fe10ad5f84f0f1179d\", \"nome\":\"BJ Suter\", \"popolarita\":21},\n" +
                                        " {\"id\":\"4ZBu9jBrNpCCuyiPUXWmZH\", \"genere\":\"j-division,\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2731185b8d55e4a0826bd8d2fb0\", \"nome\":\"Bae\", \"popolarita\":40},\n" +
                                        " {\"id\":\"4zDLgEIWWklxTSNYUM6uex\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735dd640085805699b34928fb8\", \"nome\":\"BADAK MUSIC\", \"popolarita\":22},\n" +
                                        " {\"id\":\"4ZIERQTvWH2EbE5kyQmQTy\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb86c4957c9324d7107cf24ee1\", \"nome\":\"lykia\", \"popolarita\":51},\n" +
                                        " {\"id\":\"4zmrbS8FjmjH2BceEXN7jD\", \"genere\":\"hyphy,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb6162ba8964eff3811d936a1d\", \"nome\":\"Bavgate\", \"popolarita\":29},\n" +
                                        " {\"id\":\"4ZSC7QqnOxFeyGaAb2pKnQ\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7c311face16bdab836106a3f\", \"nome\":\"Abyss\\u00e9\", \"popolarita\":39},\n" +
                                        " {\"id\":\"50PwxUMfKKNwX7ZZB1D7wg\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"Abbey Smith\", \"popolarita\":22},\n" +
                                        " {\"id\":\"53GHCxP37vt9f8h2CWNBnf\", \"genere\":\"trap queen,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb67b8468df3508559aaf71c64\", \"nome\":\"Abby Jasmine\", \"popolarita\":21},\n" +
                                        " {\"id\":\"54pzZYU7PNFAN1O5H715fk\", \"genere\":\"lo-fi emo,lo-fi indie,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb31dd183f711bf9e37d4c1538\", \"nome\":\"Nouns\", \"popolarita\":34},\n" +
                                        " {\"id\":\"56zOIRDV7DhSJVD8lZ0m2A\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3a62c10c9e6b785a29f24d44\", \"nome\":\"WORLD\", \"popolarita\":36},\n" +
                                        " {\"id\":\"58V1mBG998BXT4jR5YvIGt\", \"genere\":\"gym hardstyle,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb8ee6c6f3283153845b184692\", \"nome\":\"ily\", \"popolarita\":58},\n" +
                                        " {\"id\":\"59jYRAIZDwK4LkF578oVyC\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb90b3b156b5c4adf355666a13\", \"nome\":\"HOOL\", \"popolarita\":41},\n" +
                                        " {\"id\":\"5Dccem9dkRqim9eKHXkSd3\", \"genere\":\"japanese edm,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebc2c9608e088518305bcf6383\", \"nome\":\"BUNNY\", \"popolarita\":27},\n" +
                                        " {\"id\":\"5e9kOcXnAtmqHbgpqPQ5oF\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e4d19e8a8b42d72974bf4274\", \"nome\":\"No Shame\", \"popolarita\":34},\n" +
                                        " {\"id\":\"5Fcxa1WmqLjyxwEC3OfBLU\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2734001b79f0bd706d24a9be06a\", \"nome\":\"Babinil\", \"popolarita\":24},\n" +
                                        " {\"id\":\"5g7h24iuhLEdgrpdBOsDMY\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebc9bacfb5870b99082250bf74\", \"nome\":\"Dom!\", \"popolarita\":19},\n" +
                                        " {\"id\":\"5hrM1jc5L5aTr0qoWzEqBk\", \"genere\":\"aesthetic rap,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebfb8c84de2a5ef444537ae6b2\", \"nome\":\"Sat.urn\", \"popolarita\":32},\n" +
                                        " {\"id\":\"5iJ70tKBMdjtcC3ozMgSxm\", \"genere\":\"kawaii future bass,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb378ed36e454282fd546b13fe\", \"nome\":\"Nom Tunes\", \"popolarita\":34},\n" +
                                        " {\"id\":\"5Ilm5zQlb5LKsFpcJwR9es\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebabc8f719accdc0f2430050d5\", \"nome\":\"Abby Valdez\", \"popolarita\":40},\n" +
                                        " {\"id\":\"5L2EwdGDktjbCDfHarVMDR\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3dfbc3445e86ccd4d9a8353b\", \"nome\":\"Abbey Glover\", \"popolarita\":29},\n" +
                                        " {\"id\":\"5L9XA5GDsOOKNZfeOO8lmo\", \"genere\":\"sped up,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebf19a3d7d67007fe64b913e9b\", \"nome\":\"fam0uz\", \"popolarita\":64},\n" +
                                        " {\"id\":\"5m3CibdsoEKP2HxjyRBuGZ\", \"genere\":\"japanese hyperpop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb959dd9d33a91833119723a7b\", \"nome\":\"Baku\", \"popolarita\":24},\n" +
                                        " {\"id\":\"5mgr0FFpvy267wKVAYg8qp\", \"genere\":\"alternative metal,nu metal,pop punk,pop rock,socal pop punk,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebb55c4b1d23c8f52d0584cf19\", \"nome\":\"Lit\", \"popolarita\":55},\n" +
                                        " {\"id\":\"5ng3zK89O4P9BHqLFwkcXn\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273db8dd9c85b8b73e758823f93\", \"nome\":\"bsd.u\", \"popolarita\":44},\n" +
                                        " {\"id\":\"5nGIFgo0shDenQYSE0Sn7c\", \"genere\":\"alternative metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7ecf213c7dd78e0049379c5b\", \"nome\":\"Evanescence\", \"popolarita\":71},\n" +
                                        " {\"id\":\"5qxwWzkjL7zZQJBK4g6M24\", \"genere\":\"gym phonk,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebde324366952e0a77b14ecced\", \"nome\":\"\$ B\", \"popolarita\":26},\n" +
                                        " {\"id\":\"5tom62OiYUNIE7Sw2Ytez6\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb15e38c1f52dcb6d5ea0f9acf\", \"nome\":\"NOA|AON\", \"popolarita\":27},\n" +
                                        " {\"id\":\"5VQhfKlaFyzx7Kz1VEF2u8\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb6efffd8ee12de15f57d090aa\", \"nome\":\"BAKKA (BR)\", \"popolarita\":36},\n" +
                                        " {\"id\":\"5ZMDMg8T46GhNdD03KF9Au\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb5d60a059670eac91de9deeb8\", \"nome\":\"Bailey Flores\", \"popolarita\":20},\n" +
                                        " {\"id\":\"62EHYOwzTJYvqrvy5NXNDF\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"Ab-Liva\", \"popolarita\":26},\n" +
                                        " {\"id\":\"65lEC99xqy295XmDhg1KtW\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb38799714bf2e57bb5b58da8d\", \"nome\":\"bxkq\", \"popolarita\":35},\n" +
                                        " {\"id\":\"6aFmZaY3ydeUcYFXIJqzAT\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7eb38fe941444b9edc5bd691\", \"nome\":\"Abby Cadabby\", \"popolarita\":48},\n" +
                                        " {\"id\":\"6BYh73u2gutAYbdsbOfsqE\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273e0ab21d39ec62b760a7af94e\", \"nome\":\"NO7EM\", \"popolarita\":31},\n" +
                                        " {\"id\":\"6CrThJMQVJfWaHeliiLHuw\", \"genere\":\"scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb261c4136410f96d8cb372721\", \"nome\":\"Punkinloveee\", \"popolarita\":52},\n" +
                                        " {\"id\":\"6D170Yw0fFHseROB7UY9D1\", \"genere\":\"aesthetic rap,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eba5d76d4867a55550197fe893\", \"nome\":\"Bdr!ppyy\", \"popolarita\":30},\n" +
                                        " {\"id\":\"6FK0azeTwe5RwhUAkpkhnh\", \"genere\":\"glitchcore,scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb4eecfb32b39a839b31ebb40a\", \"nome\":\"Nosgov\", \"popolarita\":51},\n" +
                                        " {\"id\":\"6GTvw4eD65w3bVQO2BNHCP\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b273256a9c34e8640a2758c8d39e\", \"nome\":\"bbai\", \"popolarita\":23},\n" +
                                        " {\"id\":\"6gZq1Q6bdOxsUPUG1TaFbF\", \"genere\":\"alternative metal,nu metal,post-grunge,rap metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3704c6b422c3bd96836fedc1\", \"nome\":\"Godsmack\", \"popolarita\":65},\n" +
                                        " {\"id\":\"6hE7PJZZO66dPIIO5GDhpS\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7a727f74790e6ac78368bba9\", \"nome\":\"banditism.\", \"popolarita\":33},\n" +
                                        " {\"id\":\"6KO6G41BBLTDNYOLefWTMU\", \"genere\":\"alternative metal,funk metal,nu metal,rap metal,rap rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb289688a9f583f827cd834032\", \"nome\":\"P.O.D.\", \"popolarita\":60},\n" +
                                        " {\"id\":\"6ljLO5A329ym1FARh4xAz4\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2739b6b272e7a0251dd844c146a\", \"nome\":\"Nombra101\", \"popolarita\":28},\n" +
                                        " {\"id\":\"6m8acOBf7WOpC5ikjqcRTr\", \"genere\":\"pink noise,scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebeafb3eea0d1d4fee58cb463a\", \"nome\":\"zeija\", \"popolarita\":37},\n" +
                                        " {\"id\":\"6o1ntTG3W1wFDYhqWnNAlx\", \"genere\":\"aesthetic rap,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eba2e6778adaabbbe2fa37ea65\", \"nome\":\"LOAT!\", \"popolarita\":39},\n" +
                                        " {\"id\":\"6pOFMsgAzBM9T6vobjk1TK\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"BA\", \"popolarita\":2},\n" +
                                        " {\"id\":\"6q9hQKOsj8WyG2RS6PSXbV\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebab04e9ab0f18c5d2c80178ce\", \"nome\":\"Abon\", \"popolarita\":18},\n" +
                                        " {\"id\":\"6QoUf33mj2O1Wp7hIfEjXr\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb6b37f7e8888875ca4be9bf1e\", \"nome\":\"NomnomNami\", \"popolarita\":28},\n" +
                                        " {\"id\":\"6RcxmUOBnyAQr2rRsNfQI5\", \"genere\":\"modern indie pop,pov: indie,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb929e2fff120ef04df44df296\", \"nome\":\"Lincoln\", \"popolarita\":47},\n" +
                                        " {\"id\":\"6ryJRp2gIl77hK36D8tz2m\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb4a2443f2a6e3601b072ad6a9\", \"nome\":\"Abi Carter\", \"popolarita\":33},\n" +
                                        " {\"id\":\"6TnlohrPbZ8D0JvJp9S1t8\", \"genere\":\"alternative metal,funk metal,funk rock,industrial metal,nu metal,\", \"immagine\":\"https://i.scdn.co/image/922013db4ac8a22d24030c0ca88f27f21e8d0363\", \"nome\":\"Snot\", \"popolarita\":44},\n" +
                                        " {\"id\":\"6tVxgFV9M1sqlTWUnQOhlu\", \"genere\":\"corecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb4458df8245c21b083454d59b\", \"nome\":\"Maebi\", \"popolarita\":48},\n" +
                                        " {\"id\":\"6xBZgSMsnKVmaAxzWEwMSD\", \"genere\":\"alternative hip hop,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3c3f2fac45447be072333dbd\", \"nome\":\"Mike Shinoda\", \"popolarita\":52},\n" +
                                        " {\"id\":\"6xtWwGjr7pWga4C7xiG29M\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebad4ec0ce15e331fd8252c8ee\", \"nome\":\"KAYE\", \"popolarita\":51},\n" +
                                        " {\"id\":\"6XyY86QOPPrYVGvF9ch6wz\", \"genere\":\"alternative metal,nu metal,post-grunge,rap metal,rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb84a0dd74f21e8acce6a9fd49\", \"nome\":\"Linkin Park\", \"popolarita\":83},\n" +
                                        " {\"id\":\"6xzx0OH7BkbmJaBbCCzuMq\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb1c7595cd6a3c6d4c9bba4663\", \"nome\":\"AB\", \"popolarita\":17},\n" +
                                        " {\"id\":\"6zjjacSn2xSRVaFkX0tN0L\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb83e11935caf2db1d63ad210a\", \"nome\":\"ABM Drip\", \"popolarita\":18},\n" +
                                        " {\"id\":\"700c9Qz2z6HZKTumeliKAw\", \"genere\":\"chill phonk,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebebd487605b085b0d918065c5\", \"nome\":\"LONOWN\", \"popolarita\":60},\n" +
                                        " {\"id\":\"74CT3EMRU1QqEeofiVg5Fm\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb4efe858c3589d405ccb68ec3\", \"nome\":\"Noahw875\", \"popolarita\":27},\n" +
                                        " {\"id\":\"76krwUBanEdJxfdBhGehLW\", \"genere\":\"gym hardstyle,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebf7baaa44051955b89349c115\", \"nome\":\"BAKI\", \"popolarita\":56},\n" +
                                        " {\"id\":\"76lluDKzLWUhI2lEnykGnV\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb81bf4751dc7ed52061b27da2\", \"nome\":\"B-Voca\", \"popolarita\":19},\n" +
                                        " {\"id\":\"76YKoU6RezPSb7Focy72Bc\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2735679e48a6ba1445a1f0282a5\", \"nome\":\"NomosIncomplete\", \"popolarita\":30},\n" +
                                        " {\"id\":\"78SHxLdtysAXgywQ4vE0Oa\", \"genere\":\"alternative metal,alternative rock,grunge,nu metal,pop rock,post-grunge,rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebd209395dc5321f4b5163aed4\", \"nome\":\"Bush\", \"popolarita\":58},\n" +
                                        " {\"id\":\"7aNfAEH84S8JE43Nx8GKIl\", \"genere\":\"Non disponibile\", \"immagine\":\"https://www.heartoftheorient.com/wp-content/uploads/2018/08/utente-sconosciuto.png\", \"nome\":\"Abiv\", \"popolarita\":13},\n" +
                                        " {\"id\":\"7ctrKlHGrafDZ9LvSneAAM\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb0ac36d062c909cd07f443966\", \"nome\":\"ABBY M.\", \"popolarita\":36},\n" +
                                        " {\"id\":\"7CVibyURjITQGxuJV57tsU\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebdd3213ea7be93ed71fbcf672\", \"nome\":\"Baulm.\", \"popolarita\":29},\n" +
                                        " {\"id\":\"7DgNSuLgBjoROMtEQGeD1j\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7c8b4f3a17358ab5329d715f\", \"nome\":\"VIME\", \"popolarita\":19},\n" +
                                        " {\"id\":\"7dWYWUbO68rXJOcyA7SpJk\", \"genere\":\"rap rock,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb53a76aaa221c3214f14c4c21\", \"nome\":\"Fort Minor\", \"popolarita\":57},\n" +
                                        " {\"id\":\"7fc1beONoGaY0613kvQKWe\", \"genere\":\"jersey club,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebd63fa9f9c5e5b1ef0728cb37\", \"nome\":\"ProdByAbnormal\", \"popolarita\":55},\n" +
                                        " {\"id\":\"7fWgqc4HJi3pcHhK8hKg2p\", \"genere\":\"alternative metal,industrial metal,nu metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb750cc5e8908ca2a0c02d73cd\", \"nome\":\"Dope\", \"popolarita\":53},\n" +
                                        " {\"id\":\"7gissmIvS8WFUpEZnpSPMF\", \"genere\":\"rhythm game,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb09ff45f75c9f9f601e719e8d\", \"nome\":\"NOMA\", \"popolarita\":33},\n" +
                                        " {\"id\":\"7IbFR9iODFXiEfSdDfI7rU\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb941220cd06695874760ba9ed\", \"nome\":\"BAW\", \"popolarita\":27},\n" +
                                        " {\"id\":\"7iRauMPdOJBF3RiDHKrXTB\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb9078168c50456f3e109725ef\", \"nome\":\"NOS.\", \"popolarita\":35},\n" +
                                        " {\"id\":\"7JDSHlDdVTo7aZKdQZ53Vf\", \"genere\":\"alternative metal,industrial metal,industrial rock,nu metal,post-grunge,rap metal,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb7f4a72d9541571d430c157c7\", \"nome\":\"Static-X\", \"popolarita\":55},\n" +
                                        " {\"id\":\"7KBoaVRtffqjsMP5bOUoTL\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb21e2070eed2df0809f4a5744\", \"nome\":\"Bal\", \"popolarita\":18},\n" +
                                        " {\"id\":\"7kDbLquCet7fR3Mq7JEPUp\", \"genere\":\"pink noise,scenecore,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebbe93292dfd6484a2180f4054\", \"nome\":\"g0r3c0r3\", \"popolarita\":43},\n" +
                                        " {\"id\":\"7KK39gLQnZYYMZQv8RhVX9\", \"genere\":\"aesthetic rap,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb3b9ef430f65b4f523b6b7414\", \"nome\":\"Basco\", \"popolarita\":35},\n" +
                                        " {\"id\":\"7kLkBaG4ZRlT1oA8z6e8Ke\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2730e6352be6e315ce00160105b\", \"nome\":\"CODENAME\$\$\$\", \"popolarita\":14},\n" +
                                        " {\"id\":\"7noh40d76QRMnTPGW2VvO6\", \"genere\":\"hardwave,\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebcf2c6b5682c05b03e34a0a22\", \"nome\":\"Bafu\", \"popolarita\":20},\n" +
                                        " {\"id\":\"7vRSXm2InRgAWVWz6713ac\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab67616d0000b2732d965a4112e492c0393a8c7e\", \"nome\":\"BY BOY\", \"popolarita\":22},\n" +
                                        " {\"id\":\"7xqIp1044Z2vd9v9ZphjLa\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb4794dc81dd4cd91af140b704\", \"nome\":\"LYNY\", \"popolarita\":41},\n" +
                                        " {\"id\":\"7xxxjEnWtmZeBT3A4PxnY7\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5ebd9fc2b2317f22621d47a3aba\", \"nome\":\"AB001\", \"popolarita\":19},\n" +
                                        " {\"id\":\"7yYWXoHjlbeJwM2NvdjatJ\", \"genere\":\"Non disponibile\", \"immagine\":\"https://i.scdn.co/image/ab6761610000e5eb1d0b2f8dfb20d31fd475a882\", \"nome\":\"BAKUSLAYER\", \"popolarita\":28}]"

                        val artisti = Gson().fromJson(file,Array<Artisti>::class.java)
                        dao.insertAllArtist(artisti)
                        print("cc")



                    }
                })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

        ////////////////////////////////////////////////////////////////////////////////////
        // funzione che mi restituisce l'istanza del DB qualora già esistesse, altrimenti
        // se la crea e poi me la restituisce:
//        fun getDatabase(context: Context): MusicDraftDatabase? {
//            if (INSTANCE == null) {
//                synchronized(MusicDraftDatabase::class.java) {
//                    if (INSTANCE == null) {
//                        INSTANCE = androidx.room.Room.databaseBuilder(
//                            context.applicationContext,
//                            MusicDraftDatabase::class.java, "mobility_database"
//                        )
//                            // how to add a migration
//                            .addMigrations(
//
//                            )
//                            // Wipes and rebuilds instead of migrating if no Migration object.
//                            .fallbackToDestructiveMigration()
//                            .addCallback(roomDatabaseCallback)
//                            .build()
//                    }
//                }
//            }
//            return INSTANCE
//        }
//        ////////////////////////////////////////////////////////////////////////////////////
//
//
//        /**
//         * Override the onOpen method to populate the database.
//         * For this sample, we clear the database every time it is created or opened.
//         * If you want to populate the database only when the database is created for the 1st time,
//         * override MyRoomDatabase.Callback()#onCreate
//         */
//        private val roomDatabaseCallback: Callback =
//            object : Callback() {
//            }
//    }

//    abstract fun userDao(): UserDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: MusicDraftDatabase? = null
//
//        fun getInstance(context: Context): MusicDraftDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MusicDraftDatabase::class.java,
//                    "music_draft_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }

}


