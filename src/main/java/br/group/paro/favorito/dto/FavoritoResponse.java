
package br.group.paro.favorito.dto;

import br.group.paro.favorito.entity.Favorito;

public record FavoritoResponse(
        Long id,
        Long estabelecimentoId
) {
    public static FavoritoResponse fromEntity(Favorito favorito) {
        return new FavoritoResponse(
                favorito.getId(),
                favorito.getEstabelecimentoId()
        );
    }
}
