package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Package;
import at.fhtw.app.model.User;
import at.fhtw.app.persistence.DataAccessException;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageRepositoryImpl implements PackageRepository {
    private final UnitOfWork unitOfWork;

    public PackageRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void createPackage(Package pkg) throws Exception {
        String sql = "INSERT INTO packages (id, name) VALUES (?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, pkg.getId());
            stmt.setString(2, pkg.getName());
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error creating package", e);
        }
    }

    @Override
    public List<Package> getAllPackages() throws Exception {
        String sql = "SELECT * FROM packages";
        List<Package> packages = new ArrayList<>();
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                packages.add(new Package(
                        rs.getString("id"),
                        rs.getString("name"),
                        new ArrayList<>()
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching packages", e);
        }
        return packages;
    }

    @Override
    public void removePackage(String packageId) throws Exception {
        String sql = "DELETE FROM packages WHERE id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, packageId);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error removing package", e);
        }
    }

    @Override
    public int getCoinsForUser(User user) throws Exception {
        return user.getCoins();
    }



}
