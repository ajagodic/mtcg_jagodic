package at.fhtw.app.service;

import at.fhtw.app.persistence.UnitOfWork;
import at.fhtw.app.persistence.repository.StatsRepository;
import at.fhtw.app.persistence.repository.StatsRepositoryImpl;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StatsService {
    private StatsRepository statsRepository = new StatsRepositoryImpl(new UnitOfWork());

    public boolean showStats(String username) throws Exception{
        if(username!=null) {
            statsRepository.displayStats(username);
            return true;
        }
        return false;
    }
}
