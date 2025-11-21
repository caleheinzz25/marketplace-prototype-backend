package EzyShop.model.products;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dimensions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double width;
    private Double height;
    private Double depth;
}
