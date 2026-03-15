package me.anasmusa.portfolio.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.anasmusa.portfolio.api.model.AboutResponse
import me.anasmusa.portfolio.api.model.LanguageResponse
import me.anasmusa.portfolio.api.model.ProjectResponse
import me.anasmusa.portfolio.api.model.SkillResponse
import me.anasmusa.portfolio.data.model.Education
import me.anasmusa.portfolio.data.model.Experience
import me.anasmusa.portfolio.data.network.ApiClient

data class MainState(
    val about: AboutResponse? = null,
    val experience: Experience? = null,
    val education: Education? = null,
    val language: LanguageResponse? = null,
    val skills: SkillResponse? = null,
    val projects: ProjectResponse? = null,
    val isAllProjectsLoading: Boolean = false
)

sealed interface MainIntent {
    data object LoadAbout: MainIntent
    data object LoadExperience: MainIntent
    data object LoadEducation: MainIntent
    data object LoadLanguage: MainIntent
    data object LoadSkill: MainIntent
    data class LoadProjects(val loadAll: Boolean): MainIntent
}

class MainViewModel: ViewModel(){

    private val apiClient = ApiClient()

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState>
        get() = _state


    fun handle(intent: MainIntent){
        when(intent){
            MainIntent.LoadAbout -> loadAbout()
            MainIntent.LoadExperience -> loadExperience()
            MainIntent.LoadEducation -> loadEducation()
            MainIntent.LoadLanguage -> loadLanguage()
            MainIntent.LoadSkill -> loadSkills()
            is MainIntent.LoadProjects -> loadProjects(isPrimary = if (intent.loadAll) null else true)
        }
    }

   private fun loadAbout() {
       if (state.value.about != null) return
       viewModelScope.launch {
           try {
               _state.update {
                   it.copy(about = apiClient.getAbout().data)
               }
           } catch (_: Exception){}
       }
   }

    private fun loadExperience() {
        if (state.value.experience != null) return
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        experience = apiClient.getExperience().data.let { response ->
                            Experience(
                                response.entities.map {
                                    Experience.Entity(
                                        it.company,
                                        it.link,
                                        it.date,
                                        it.position,
                                        it.items.map { it.value }
                                    )
                                }
                            )
                        }
                    )
                }
            } catch (_: Exception){}
        }
    }

    private fun loadEducation() {
        if (state.value.education != null) return
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        education = apiClient.getEducation().data.let { response ->
                            Education(
                                response.entities.map {
                                    Education.Entity(
                                        university = it.university,
                                        field = it.field,
                                        completed = it.completed,
                                        date = it.date,
                                        items = it.items.map { it.value }
                                    )
                                }
                            )
                        }
                    )
                }
            } catch (_: Exception){}
        }
    }

    private fun loadLanguage() {
        if (state.value.language != null) return
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        language = apiClient.getLanguage().data
                    )
                }
            } catch (_: Exception){}
        }
    }

    private fun loadSkills() {
        if (state.value.skills != null) return
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(skills = apiClient.getSkills().data)
                }
            } catch (_: Exception){}
        }
    }

    private fun loadProjects(isPrimary: Boolean?) {
        if (state.value.projects != null && isPrimary != null) return
        if (isPrimary == null) {
            _state.update {
                it.copy(isAllProjectsLoading = true)
            }
        }
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        projects = apiClient.getProjects(isPrimary = isPrimary).data,
                        isAllProjectsLoading = false
                    )
                }
            } catch (_: Exception){}
        }
    }

}