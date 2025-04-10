package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.Account;
import kc.wheremybuckgoes.modal.Settings;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.repositories.SettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingsService {
    public final SettingsRepository settingsRepository;

    public Settings getSettingsById(Long id){
        return settingsRepository.findById(id).orElse(null);
    }

    public Settings createOrUpdateSettings(Settings settings){
        log.info("SettingsService: createOrUpdateSettings()");
        return settingsRepository.save(settings);
    }

    public Settings getSettingByUser(User user){
        log.info("SettingsService: getSettingByUser()");
        Settings settings = settingsRepository.findByCreatedBy(user).orElse(null);
        if(settings == null){
            settings = Settings.builder().createdBy(user).accounts(new Account[]{}).theme(ApplicationConstant.Theme.LIGHT).categories(new String[]{}).build();
            settings = createOrUpdateSettings(settings);
        }
        return settings;
    }
}
