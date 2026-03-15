package com.boilerplate.strains;


import com.boilerplate.strains.dto.StrainCreationRequestDTO;
import com.boilerplate.strains.dto.StrainCreationResponseDTO;
import com.boilerplate.strains.dto.StrainsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/strains")
@RequiredArgsConstructor
public class StrainController {

    private final StrainService strainService;
    @PostMapping
    public StrainCreationResponseDTO createStrain(@RequestBody StrainCreationRequestDTO creationRequestDTO){
        return strainService.createStrain(creationRequestDTO);
    }
    @GetMapping
    public StrainsResponseDTO getAll(){
        return strainService.getAll();
    }
}
