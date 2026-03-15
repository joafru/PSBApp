package com.boilerplate.strains;

import com.boilerplate.strains.dto.StrainCreationRequestDTO;
import com.boilerplate.strains.dto.StrainCreationResponseDTO;
import com.boilerplate.strains.dto.StrainResponseDTO;
import com.boilerplate.strains.dto.StrainsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StrainService {

    private final StrainRepository strainRepository;

    public StrainCreationResponseDTO createStrain(StrainCreationRequestDTO creationRequestDTO) {
        Strain strain = new Strain();
        strain.setName(creationRequestDTO.getName());
        strain.setLbcc(creationRequestDTO.getLbcc());
        strain.setStrainDate(creationRequestDTO.getStrainDate());
        strainRepository.save(strain);
        return new StrainCreationResponseDTO(strain.getName(), strain.getLbcc(), strain.getStrainDate());
    }

    public StrainsResponseDTO getAll() {
        StrainsResponseDTO response = new StrainsResponseDTO();
        List<Strain> strains = strainRepository.findAll();
        List<StrainResponseDTO> strainResponse = strains
                .stream()
                .map(s->{
                        return new StrainResponseDTO(s.getName(), s.getLbcc(), s.getStrainDate());
                    })
                .toList();
        response.setStrains(strainResponse);
        return response;
    }
}
