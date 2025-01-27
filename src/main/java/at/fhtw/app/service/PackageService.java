package at.fhtw.app.service;

import at.fhtw.app.model.Package;
import at.fhtw.app.persistence.UnitOfWork;
import at.fhtw.app.persistence.repository.PackageRepository;
import at.fhtw.app.persistence.repository.PackageRepositoryImpl;
import at.fhtw.app.persistence.repository.UserRepositoryImpl;

public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService() {
        this.packageRepository = new PackageRepositoryImpl(new UnitOfWork());
    }

    public void addPackage(Package pkg) throws Exception {
        packageRepository.createPackage(pkg);
    }

    public Package buyPackage() throws Exception {
        Package pkg = packageRepository.fetchPackage();
        if (pkg == null) {
            throw new IllegalStateException("No packages available to buy.");
        }
        packageRepository.removePackage(pkg.getId());
        return pkg;
    }
}
