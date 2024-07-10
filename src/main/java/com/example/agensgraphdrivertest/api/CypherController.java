package com.example.agensgraphdrivertest.api;

import com.example.agensgraphdrivertest.model.ResponseEntityFactory;
import com.example.agensgraphdrivertest.service.CypherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/graphizer-global/v1/cypher")
public class CypherController {

    private final CypherService cypherService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCypher(@RequestParam String query) throws SQLException, ClassNotFoundException {
//        return ResponseEntityFactory.ok(cypherService.getCypher(query));
        return ResponseEntityFactory.ok(cypherService.getCypherByAgensGraph(query));
//        return ResponseEntityFactory.ok(cypherService.getCypherByAgensGraphByPsmt(query));
//        List<String> queryList = new ArrayList<>();
//        queryList.add(query);
//        queryList.add("MATCH (v1:ott{ott:\"Netflix\"})-[e:provide]->(v2{방영년도:\"2021년\"}) return v1, e, v2");
//        return ResponseEntityFactory.ok(cypherService.getMultipleCypher(queryList, "graphc_4"));
    }
}
