package me.anasmusa.portfolio.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.anasmusa.portfolio.api.model.AboutResponse
import me.anasmusa.portfolio.api.model.LanguageResponse
import me.anasmusa.portfolio.api.model.Platform
import me.anasmusa.portfolio.api.model.SkillResponse
import me.anasmusa.portfolio.data.model.Education
import me.anasmusa.portfolio.data.model.Experience
import me.anasmusa.portfolio.data.model.Project
import me.anasmusa.portfolio.data.model.Project.PlatformInfo
import me.anasmusa.portfolio.data.model.toUI
import me.anasmusa.portfolio.data.network.ApiClient

data class MainState(
    val about: AboutResponse? = null,
    val experience: Experience? = null,
    val education: Education? = null,
    val language: LanguageResponse? = null,
    val skills: SkillResponse? = null,
    val projects: List<Project>? = null,
    val totalProjectsCount: Int = 0,
    val platforms: List<PlatformInfo> = emptyList(),
    val selectedPlatform: Platform? = null,
    val isAllProjectsLoading: Boolean = false
)

sealed interface MainIntent {
    data object LoadAbout: MainIntent
    data object LoadExperience: MainIntent
    data object LoadEducation: MainIntent
    data object LoadLanguage: MainIntent
    data object LoadSkill: MainIntent
    data class LoadProjects(val loadAll: Boolean): MainIntent
    data class SelectPlatform(val platform: Platform?): MainIntent
}

class MainViewModel: ViewModel(){

    private val apiClient = ApiClient()

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState>
        get() = _state

    private var cachedProjects: List<Project>? = null
    private var projectLoadJob: Job? = null

    fun handle(intent: MainIntent){
        when(intent){
            MainIntent.LoadAbout -> loadAbout()
            MainIntent.LoadExperience -> loadExperience()
            MainIntent.LoadEducation -> loadEducation()
            MainIntent.LoadLanguage -> loadLanguage()
            MainIntent.LoadSkill -> loadSkills()
            is MainIntent.LoadProjects -> loadProjects(isPrimary = if (intent.loadAll) null else true)
            is MainIntent.SelectPlatform -> filterProjects(intent.platform)
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
        if (cachedProjects != null && isPrimary != null) return
        viewModelScope.launch {
            projectLoadJob?.join()

            if (cachedProjects != null && cachedProjects!!.size == state.value.totalProjectsCount) return@launch
            projectLoadJob = launch {
                if (isPrimary == null) {
                    _state.update {
                        it.copy(isAllProjectsLoading = true)
                    }
                }

                try {
                    val response = apiClient.getProjects(isPrimary = isPrimary).data
                    if (cachedProjects == null){
                        _state.update {
                            it.copy(
                                totalProjectsCount = response.totalCount,
                                platforms = response.platforms.map { it.toUI() }
                            )
                        }
                    }
                    cachedProjects = response.toUI()
                    submitProjects(state.value.selectedPlatform)
                } catch (_: Exception){
                    _state.update { it.copy(isAllProjectsLoading = false) }
                }
            }
        }
    }

    private fun filterProjects(platform: Platform?) {
        _state.update {
            it.copy(
                projects = null,
                selectedPlatform = platform,
                isAllProjectsLoading = false
            )
        }
        viewModelScope.launch {
            projectLoadJob?.join()
            _state.update {
                it.copy(
                    projects = null,
                    isAllProjectsLoading = false
                )
            }
            if (cachedProjects != null && cachedProjects!!.size != state.value.totalProjectsCount) {
                val response = apiClient.getProjects(isPrimary = null).data
                cachedProjects = response.toUI()
            }
            submitProjects(platform)
        }
    }

    private fun submitProjects(platform: Platform?) {
        cachedProjects?.let { cachedProjects ->
            _state.update {
                it.copy(
                    projects = if (platform == null)
                        cachedProjects
                    else
                        cachedProjects.filter {
                            it.platforms.contains(platform)
                        }
                    ,
                    isAllProjectsLoading = false
                )
            }
        }
    }

}