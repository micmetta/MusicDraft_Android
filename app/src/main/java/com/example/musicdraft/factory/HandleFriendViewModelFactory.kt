import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicdraft.viewModel.HandleFriendsViewModel

class HandleFriendViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HandleFriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HandleFriendsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}