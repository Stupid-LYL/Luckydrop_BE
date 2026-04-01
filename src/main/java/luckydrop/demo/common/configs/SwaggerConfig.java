package luckydrop.demo.common.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "LuckyDrop API",
                version = "v1.0.0",
                description = """
                        LuckyDrop 드로우 응모 서비스 API 문서입니다.
                        
                        [주요기능]
                        - 회원 인증
                        - 드로우 생성 / 조회 / 응모 / 추첨
                        - 북마크
                        - 관리자 강제 취소
                        """,
                contact = @Contact(
                        name = "LuckyDrop Team - StupidLYL",
                        email = "hholly18@gmail.com"
                ),
                license = @License(
                        name = "Internal Use Only"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Server")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Access Token을 입력하세요. 예: eyJhbGciOiJIUzI1NiJ9..."
)
public class SwaggerConfig {
}
