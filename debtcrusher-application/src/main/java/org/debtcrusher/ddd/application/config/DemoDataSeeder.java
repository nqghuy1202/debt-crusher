package org.debtcrusher.ddd.application.config;

import org.debtcrusher.ddd.application.service.AuthAppService;
import org.debtcrusher.ddd.application.service.DebtAppService;
import org.debtcrusher.ddd.domain.model.User;
import org.debtcrusher.ddd.domain.model.enums.DebtCategory;
import org.debtcrusher.ddd.domain.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Tạo tài khoản demo (khớp DEMO_CREDENTIALS ở frontend/src/pages/LoginPage.tsx) kèm 20 khoản nợ mẫu
 * lúc app khởi động, nếu chưa có — environment/mysql/init chỉ tạo schema, không seed data, nên trên
 * DB production mới tinh nút "Dùng tài khoản demo" sẽ đăng nhập thất bại (user không tồn tại) nếu
 * thiếu bước này.
 */
@Component
public class DemoDataSeeder implements ApplicationRunner {

    private static final String DEMO_EMAIL = "demo@debtcrusher.vn";
    private static final String DEMO_PASSWORD = "Demo@2026";

    private final AuthAppService authAppService;
    private final DebtAppService debtAppService;
    private final UserRepository userRepository;

    public DemoDataSeeder(AuthAppService authAppService, DebtAppService debtAppService,
                           UserRepository userRepository) {
        this.authAppService = authAppService;
        this.debtAppService = debtAppService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        User demoUser = userRepository.findByEmail(DEMO_EMAIL)
                .orElseGet(() -> authAppService.register(DEMO_EMAIL, DEMO_PASSWORD));

        if (debtAppService.listDebts(demoUser.getId()).isEmpty()) {
            seedDebts(demoUser.getId());
        }
    }

    private void seedDebts(Long userId) {
        Object[][] debts = {
                {"Thẻ tín dụng Vietcombank", "45000000", "24.9", "2500000", DebtCategory.CREDIT_CARD},
                {"Thẻ tín dụng Techcombank", "28000000", "22.5", "1800000", DebtCategory.CREDIT_CARD},
                {"Thẻ tín dụng Sacombank", "15500000", "26.0", "1200000", DebtCategory.CREDIT_CARD},
                {"Vay tiêu dùng FE Credit", "60000000", "35.0", "3200000", DebtCategory.CONSUMER_LOAN},
                {"Vay tiêu dùng Home Credit", "22000000", "32.5", "1500000", DebtCategory.CONSUMER_LOAN},
                {"Vay tiền mặt ngân hàng ACB", "80000000", "14.5", "4000000", DebtCategory.CONSUMER_LOAN},
                {"Trả góp điện thoại iPhone 16", "18000000", "0", "1500000", DebtCategory.INSTALLMENT},
                {"Trả góp laptop Dell XPS", "24000000", "0", "2000000", DebtCategory.INSTALLMENT},
                {"Trả góp xe máy Honda SH", "35000000", "12.0", "1800000", DebtCategory.INSTALLMENT},
                {"Trả góp nội thất Nhà Xinh", "12000000", "0", "1000000", DebtCategory.INSTALLMENT},
                {"Vay học phí đại học", "50000000", "6.5", "1200000", DebtCategory.STUDENT_LOAN},
                {"Vay học cao học nước ngoài", "150000000", "5.0", "3000000", DebtCategory.STUDENT_LOAN},
                {"Vay mua căn hộ chung cư", "1200000000", "9.5", "15000000", DebtCategory.MORTGAGE},
                {"Vay mua nhà đất", "800000000", "10.2", "9500000", DebtCategory.MORTGAGE},
                {"Vay sửa nhà", "150000000", "11.0", "3500000", DebtCategory.MORTGAGE},
                {"Vay bạn bè", "10000000", "0", "1000000", DebtCategory.OTHER},
                {"Vay người thân", "20000000", "0", "2000000", DebtCategory.OTHER},
                {"Vay công ty tài chính khác", "30000000", "28.0", "2200000", DebtCategory.OTHER},
                {"Thẻ tín dụng VPBank", "9500000", "27.5", "800000", DebtCategory.CREDIT_CARD},
                {"Vay tiêu dùng Mcredit", "17000000", "30.0", "1300000", DebtCategory.CONSUMER_LOAN},
        };

        for (Object[] d : debts) {
            debtAppService.createDebt(
                    userId,
                    (String) d[0],
                    new BigDecimal((String) d[1]),
                    new BigDecimal((String) d[2]),
                    new BigDecimal((String) d[3]),
                    (DebtCategory) d[4]);
        }
    }
}
