import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicdraft.viewModel.HandleFriendsViewModel

/**
 * Factory per la creazione di istanze di [HandleFriendsViewModel].
 *
 * Questa factory viene utilizzata per fornire le dipendenze necessarie al ViewModel,
 * in questo caso solo [Application].
 *
 * @property application Oggetto Application per l'accesso al contesto dell'applicazione.
 */
class HandleFriendViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Crea una nuova istanza del ViewModel specificato.
     *
     * @param modelClass Classe del ViewModel da creare.
     * @return Istanzia un oggetto di tipo [HandleFriendsViewModel] se il modelClass è compatibile,
     * altrimenti lancia un'eccezione [IllegalArgumentException].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HandleFriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HandleFriendsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}